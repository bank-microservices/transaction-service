package com.nttdata.microservices.transaction.service.impl;

import com.nttdata.microservices.transaction.entity.TransactionType;
import com.nttdata.microservices.transaction.exception.AccountNotFoundException;
import com.nttdata.microservices.transaction.proxy.AccountProxy;
import com.nttdata.microservices.transaction.repository.TransactionRepository;
import com.nttdata.microservices.transaction.service.TransactionService;
import com.nttdata.microservices.transaction.service.dto.TransactionDto;
import com.nttdata.microservices.transaction.service.mapper.TransactionMapper;
import com.nttdata.microservices.transaction.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

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
            .map(txMapper::toEntity)
            .map(entity -> {
              entity.setRegisterDate(LocalDateTime.now());
              entity.setTransactionCode(NumberUtil.generateRandomNumber(8));
              entity.setTransactionType(TransactionType.DEPOSIT);
              return entity;
            })
            .flatMap(txRepository::save)
            .map(txMapper::toDto)
            .flatMap(this::updateAccountAmount)
            .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Mono<TransactionDto> withdraw(TransactionDto transactionDto) {
    return Mono.just(transactionDto)
            .flatMap(this::existAccount)
            .map(txMapper::toEntity)
            .map(entity -> {
              entity.setRegisterDate(LocalDateTime.now());
              entity.setTransactionCode(NumberUtil.generateRandomNumber(8));
              entity.setTransactionType(TransactionType.WITHDRAWAL);
              return entity;
            })
            .flatMap(txRepository::save)
            .map(txMapper::toDto)
            .flatMap(this::updateAccountAmount)
            .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<TransactionDto> existAccount(TransactionDto transactionDto) {
    return this.accountProxy.findByAccountNumber(transactionDto.getAccountNumber())
            .switchIfEmpty(Mono.error(new AccountNotFoundException(getMsg("account.not.found"))))
            .doOnNext(transactionDto::setAccount)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .thenReturn(transactionDto);
  }

  private Mono<TransactionDto> updateAccountAmount(TransactionDto transactionDto) {
    final double amount = transactionDto.getTransactionType().isDeposit() ?
            transactionDto.getAmount() : transactionDto.getAmount() * -1;
    return this.accountProxy.updateAccountAmount(transactionDto.getAccount().getId(), amount)
            .doOnNext(transactionDto::setAccount)
            .thenReturn(transactionDto);
  }

}
