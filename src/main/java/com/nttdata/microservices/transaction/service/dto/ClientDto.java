package com.nttdata.microservices.transaction.service.dto;

import com.nttdata.microservices.transaction.entity.client.ClientType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientDto {

  private String id;

  private String documentNumber;

  private String firstNameBusiness;

  private String surnames;

  private ClientType clientType;
}
