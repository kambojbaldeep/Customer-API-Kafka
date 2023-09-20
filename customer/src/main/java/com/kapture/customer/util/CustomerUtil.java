package com.kapture.customer.util;

import com.kapture.customer.dto.CustomerResponseObject;
import com.kapture.customer.model.Customer;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomerUtil {

    public static Timestamp getCurrentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static boolean checkInputFields(Customer customer) {
        return (customer != null) && (customer.getClientId() != 0) && (customer.getName() != null && !customer.getName().isEmpty()) && (customer.getEmail() != null && !customer.getEmail().isEmpty()) && (customer.getPhoneNo() != null && !customer.getPhoneNo().isEmpty());
    }

    public static int checkInputFieldsForCustomerId(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(customerId);
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int checkInputFieldsForClientId(String clientId) {
        if ((clientId == null || clientId.isEmpty())) {
            return 0;
        }
        try {
            int value = Integer.parseInt(clientId);
            return value;

        } catch (Exception e) {
            return 0;
        }
    }

    public static String generateCustomerCode() {
        return "CODE" + String.valueOf(System.currentTimeMillis());
    }


    public static boolean checkCustomerPhoneNo(Customer customer) {
        String phone = customer.getPhoneNo();
        if (phone != null && !phone.isEmpty()) {
//            return phone.matches("(\\+91)?(-)?\\s*?(91)?\\s*?(\\d{3})-?\\s*?(\\d{3})-?\\s*?(\\d{4})");
            String regexPattern = "\\+91\\d{10}";
            boolean isValidPhoneNo = Pattern.matches(regexPattern, customer.getPhoneNo());
            if (!isValidPhoneNo) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkCustomerEmail(Customer customer) {
        String email = customer.getEmail();
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static List<CustomerResponseObject.CustomerDetails> customerListToResponseList(List<Customer> customerList) {
        List<CustomerResponseObject.CustomerDetails> customerDetailsList = new ArrayList<>();
        for (Customer customer : customerList) {
            CustomerResponseObject.CustomerDetails customerDetails = new CustomerResponseObject.CustomerDetails();
            customerDetails.setCustomerName(customer.getName());
            customerDetails.setCustomerEmail(customer.getEmail());
            customerDetails.setCustomerId(customer.getId());
            customerDetails.setCustomerCode(customer.getCustomerCode());
            customerDetailsList.add(customerDetails);
        }
        return customerDetailsList;
    }


}
