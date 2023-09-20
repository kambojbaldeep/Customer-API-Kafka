package com.kapture.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CustomerResponseObjectByClientId {
    private String message;
    private boolean status;
    List<CustomerResponseObject.CustomerDetails> customerDetailsList;

}
