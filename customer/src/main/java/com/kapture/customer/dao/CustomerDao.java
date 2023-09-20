package com.kapture.customer.dao;

import com.kapture.customer.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    Customer findCustomerById(int customerId);

    Customer addCustomer(Customer customer);

    Optional<Customer> findCustomerByCustomerCode(String customerCode);

    List<Customer> getCustomersByClientIdWithPagination(int clientId, int offset, int pageSize);

    Optional<Customer> findCustomerByEmail(String email);

    Optional<Customer> findCustomerByPhone(String phoneNo);

}
