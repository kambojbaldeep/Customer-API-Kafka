package com.kapture.customer.controller;

import com.kapture.customer.service.CustomerService;
import com.kapture.customer.dto.ClientIdDto;
import com.kapture.customer.dto.CustomerIdDto;
import com.kapture.customer.dto.CustomerResponseObject;
import com.kapture.customer.dto.CustomerResponseObjectByClientId;
import com.kapture.customer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    @Autowired
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/get-customer-by-client-id")
    public ResponseEntity<CustomerResponseObjectByClientId> getCustomerByClientId(@RequestBody ClientIdDto clientIdDto) {
        return customerService.getCustomerListByClientId(clientIdDto.getClientId(), clientIdDto.getPageNo(), clientIdDto.getPageSize());
    }

    @GetMapping("/get-customer-by-id")
    public ResponseEntity<CustomerResponseObject> getCustomerById(@RequestBody CustomerIdDto customerIdDto) {
        return customerService.getCustomerById(customerIdDto.getCustomerId());
    }

    @PostMapping("/add-update-customer")
    public ResponseEntity<CustomerResponseObject> saveOrUpdate(@RequestBody Customer customer) {
        return customerService.saveOrUpdateCustomer(customer);
    }

    @PostMapping("/allCustomer/upload")
    public String uploadAllCustomer(@RequestParam("file")MultipartFile multipartFile){
//        if (Helper.checkExcelFormat(multipartFile)){
            return customerService.saveOrUpdateExcelDataToDb(multipartFile);
//        }
    }


}
