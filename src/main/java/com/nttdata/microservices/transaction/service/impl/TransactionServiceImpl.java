package com.nttdata.microservices.transaction.service.impl;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

import com.nttdata.microservices.transaction.entity.TransactionType;
import com.nttdata.microservices.transaction.exception.AccountNotFoundException;
import com.nttdata.microservices.transaction.exception.BadRequestException;
import com.nttdata.microservices.transaction.proxy.AccountProxy;
import com.nttdata.microservices.transaction.repository.TransactionRepository;
import com.nttdata.microservices.transaction.service.TransactionService;
import com.nttdata.microservices.transaction.service.dto.AccountDto;
import com.nttdata.microservices.transaction.service.dto.TransactionDto;
import com.nttdata.microservices.transaction.service.dto.TransactionTransferRequestDto;
import com.nttdata.microservices.transaction.service.mapper.TransactionMapper;
import com.nttdata.microservices.transaction.util.NumberUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository txRepository;
  private final AccountProxy accountProxy;

  private final TransactionMapper txMapper;

  @Override
  public Flux<TransactionDto> findAll() {
    return txRepository.findAll()
        .map(txMapper::toDto);
  }

  @Override
  public Flux<TransactionDto> findByAccountId(String accountId) {
    return txRepository.findByAccountId(accountId)
        .map(txMapper::toDto);
  }

  @Override
  public Flux<TransactionDto> findByAccountNumber(String accountNumber) {
    return txRepository.findByAccountNumber(accountNumber)
        .map(txMapper::toDto);
  }

  @Override
  public Mono<TransactionDto> deposit(TransactionDto transactionDto) {
    return Mono.just(transactionDto)
        .flatMap(this::existAccount)
        .flatMap(this::validateTransactionsOfMonth)
        .flatMap(dto -> this.transaction(dto, TransactionType.DEPOSIT))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<TransactionDto> withdraw(TransactionDto transactionDto) {
    return Mono.just(transactionDto)
        .flatMap(this::existAccount)
        .flatMap(this::validateBalance)
        .flatMap(this::validateTransactionsOfMonth)
        .flatMap(dto -> this.transaction(dto, TransactionType.WITHDRAWAL))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Flux<TransactionDto> transfer(TransactionTransferRequestDto transferDto) {
    return Flux.just(transferDto)
        .flatMap(this::existAccounts)
        .flatMap((dto) -> {
          dto.setAmount(transferDto.getAmount());
          if (dto.getAccountNumber().equals(transferDto.getSourceAccountNumber())) {
            return Mono.just(dto)
                .flatMap(this::validateBalance)
                .flatMap(this::validateTransactionsOfMonth)
                .flatMap(transDto -> this.transaction(transDto, TransactionType.WITHDRAWAL));
          } else {
            return Mono.just(dto)
                .flatMap(transDto -> this.transaction(transDto, TransactionType.DEPOSIT));
          }
        })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<TransactionDto> transaction(TransactionDto transactionDto,
      TransactionType transactionType) {
    return Mono.just(transactionDto)
        .map(txMapper::toEntity)
        .map(entity -> {

          if (entity.getTransactionFee() == null) {
            entity.setTransactionFee(0D);
            entity.setTotalAmount(entity.getAmount());
          }

          entity.setRegisterDate(LocalDateTime.now());
          entity.setTransactionCode(NumberUtil.generateRandomNumber(8));
          entity.setTransactionType(transactionType);
          return entity;
        })
        .flatMap(txRepository::save)
        .map(txMapper::toDto)
        .flatMap(this::updateAccountAmount);
  }

  private Flux<TransactionDto> existAccounts(TransactionTransferRequestDto transferDto) {
    final TransactionDto sourceAccount = TransactionDto.builder()
        .accountNumber(transferDto.getSourceAccountNumber()).build();
    final TransactionDto targetAccount = TransactionDto.builder()
        .accountNumber(transferDto.getTargetAccountNumber()).build();
    return Flux.just(sourceAccount, targetAccount)
        .flatMap(this::existAccount);
  }

  private Mono<TransactionDto> existAccount(TransactionDto transactionDto) {
    return this.accountProxy.findByAccountNumber(transactionDto.getAccountNumber())
        .switchIfEmpty(Mono.error(new AccountNotFoundException(getMsg("account.not.found"))))
        .doOnNext(transactionDto::setAccount)
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
        .thenReturn(transactionDto);
  }

  private Mono<TransactionDto> validateBalance(TransactionDto transactionDto) {
    return Mono.just(transactionDto)
        .<TransactionDto>handle((dto, sink) -> {
          final Double amountAccount = dto.getAccount().getAmount();
          if (dto.getAmount() > amountAccount) {
            sink.error(new BadRequestException(getMsg("account.balance.not.available",
                dto.getAccountNumber(), dto.getAmount())));
          } else {
            sink.complete();
          }
        })
        .thenReturn(transactionDto);
  }

  private Mono<TransactionDto> validateTransactionsOfMonth(TransactionDto transactionDto) {
    int currentMonth = LocalDate.now().getMonthValue();
    int currentYear = LocalDate.now().getYear();

    final Mono<Long> countTxs = txRepository.findTransactionsMonthByAccountNumber(
        transactionDto.getAccountNumber(), currentMonth, currentYear).count();

    return Mono.zip(countTxs, Mono.just(transactionDto))
        .map(objects -> {
          final Long countTx = objects.getT1();
          log.debug("Count transaction: {} of Month: {} and Year: {}",
              countTx, currentMonth, currentYear);
          final TransactionDto dto = objects.getT2();
          final AccountDto account = dto.getAccount();
          if (countTx >= account.getMaxLimitMonthlyMovements()) {

            if (account.getTransactionFee() != null) {
              final double additional = dto.getAmount()
                  * ObjectUtils.defaultIfNull(account.getTransactionFee(), 1D);

              final Double totalAmount = Double.sum(dto.getAmount(), additional);
              dto.setTransactionFee(additional);
              dto.setTotalAmount(totalAmount);
            } else {
              dto.setTransactionFee(0D);
              dto.setTotalAmount(dto.getAmount());
            }
            
          } else {
            dto.setTransactionFee(0D);
            dto.setTotalAmount(dto.getAmount());
          }
          return dto;
        });

  }

  private Mono<TransactionDto> updateAccountAmount(TransactionDto transactionDto) {
    final double amount = transactionDto.getTransactionType().isDeposit()
        ? transactionDto.getAmount()
        : transactionDto.getAmount() * -1;

    return this.accountProxy.updateAccountAmount(transactionDto.getAccount().getId(), amount)
        .doOnNext(transactionDto::setAccount)
        .thenReturn(transactionDto);
  }

  @Override
  public Flux<TransactionDto> findByDateRange(String from, String to) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate fromDate = LocalDate.parse(from, formatter);
    LocalDate toDate = LocalDate.parse(to, formatter);
    return txRepository.findByRegisterDateBetween(fromDate, toDate)
        .map(txMapper::toDto);
  }

}
