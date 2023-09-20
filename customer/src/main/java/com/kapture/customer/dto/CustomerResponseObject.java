package com.kapture.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CustomerResponseObject {
    private List<CustomerDetails> customerList;

    private CustomerDetails customerDetails;
    private boolean status;
    private  String message;

    @Data
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static
    class CustomerDetails {
        private int customerId;
        private String customerCode;
        private String customerName;
        private String customerEmail;
    }
}
