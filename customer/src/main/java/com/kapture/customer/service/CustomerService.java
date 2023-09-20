package com.kapture.customer.service;

import com.kapture.customer.constant.Constants;
import com.kapture.customer.dao.CustomerDao;
import com.kapture.customer.dto.CustomerResponseObject;
import com.kapture.customer.dto.CustomerResponseObjectByClientId;
import com.kapture.customer.listener.Consumer;
import com.kapture.customer.model.Customer;
import com.kapture.customer.util.CustomerUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private KafkaTemplate<String, Customer> kafkaTemplate;

    private Consumer consumer;
    @Value("${spring.kafka.test.topic}")
    private String topicName;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    public ResponseEntity<CustomerResponseObject> getCustomerById(String customerIdString) {
        CustomerResponseObject responseObject = new CustomerResponseObject();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            int customerId = CustomerUtil.checkInputFieldsForCustomerId(customerIdString);
            if (customerId <= 0) {
                responseObject.setMessage(Constants.INVALID_INPUT);
                LOGGER.error("Invalid input");
            } else {
                    Customer customer = customerDao.findCustomerById(customerId);
                    if (customer == null) {
                        responseObject.setMessage(Constants.NOT_FOUND_BY_ID + customerId);
                        httpStatus = HttpStatus.NOT_FOUND;
                    }

                if (customer != null) {
                    CustomerResponseObject.CustomerDetails customerDetails = new CustomerResponseObject.CustomerDetails();
                    responseObject.setStatus(true);
                    customerDetails.setCustomerId(customer.getId());
                    customerDetails.setCustomerCode(customer.getCustomerCode());
                    customerDetails.setCustomerEmail(customer.getEmail());
                    customerDetails.setCustomerName(customer.getName());
                    responseObject.setCustomerDetails(customerDetails);
                    responseObject.setMessage(Constants.DATA_FOUND);
                }
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.error("Failed to load customer", e);
        }
        return new ResponseEntity<>(responseObject, httpStatus);
    }


    public ResponseEntity<CustomerResponseObjectByClientId> getCustomerListByClientId(String clientIdString, String pageNoString, String pageSizeString) {
        CustomerResponseObjectByClientId responseObject = new CustomerResponseObjectByClientId();
        HttpStatus httpStatus = HttpStatus.OK;
        int clientId = CustomerUtil.checkInputFieldsForClientId(clientIdString);
        if (clientId <= 0) {
            responseObject.setMessage("Invalid input");
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            try {
                int pageNo = Integer.parseInt(pageNoString);
                int pageSize = Integer.parseInt(pageSizeString);
                int offset = (pageNo - 1) * pageSize;
                List<Customer> customerList = customerDao.getCustomersByClientIdWithPagination(clientId, offset, pageSize);
                if (customerList == null || customerList.isEmpty()) {
                    responseObject.setMessage(Constants.NO_CUSTOMER_ASSOCIATED_WITH_CLIENT_ID + clientId);
                } else {
                    responseObject.setMessage(Constants.DATA_FOUND);
                    responseObject.setCustomerDetailsList(CustomerUtil.customerListToResponseList(customerList));
                    responseObject.setStatus(true);
                    LOGGER.info("Data Found Successfully");
                }

            } catch (Exception e) {
                httpStatus = HttpStatus.BAD_REQUEST;
                responseObject.setMessage(Constants.FAILED_TO_LOAD_CUSTOMER_LIST);
                LOGGER.error("Failed to load customerList", e);
            }
        }
        return new ResponseEntity<>(responseObject, httpStatus);
    }


    public ResponseEntity<CustomerResponseObject> saveOrUpdateCustomer(Customer customer) {
        CustomerResponseObject responseObject = new CustomerResponseObject();
        HttpStatus httpStatus = HttpStatus.OK;
        if (!CustomerUtil.checkInputFields(customer)) {
            responseObject.setMessage("Enter all fields");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Optional<Customer> email = customerDao.findCustomerByEmail(customer.getEmail());
        if (email.isPresent()) {
            responseObject = new CustomerResponseObject();
            responseObject.setMessage("email already exist");
            return new ResponseEntity<>(responseObject, httpStatus);
        }
        Optional<Customer> phone = customerDao.findCustomerByPhone(customer.getPhoneNo());
        if (phone.isPresent()) {
            responseObject.setMessage("phoneNo already exist");
            return new ResponseEntity<>(responseObject, httpStatus);
        }
        if (!CustomerUtil.checkCustomerPhoneNo(customer)) {
            responseObject.setMessage("Invalid phone number");
            return new ResponseEntity<>(responseObject, httpStatus);
        }
        if (!CustomerUtil.checkCustomerEmail(customer)) {
            responseObject.setMessage("Invalid email address");
            return new ResponseEntity<>(responseObject, httpStatus);
        }
        try {
            customer.setCustomerCode(CustomerUtil.generateCustomerCode());
            CustomerResponseObject.CustomerDetails customerDetails = new CustomerResponseObject.CustomerDetails();
            customerDetails.setCustomerCode(customer.getCustomerCode());
            customerDetails.setCustomerName(customer.getName());
            customerDetails.setCustomerEmail(customer.getEmail());
            responseObject.setCustomerDetails(customerDetails);
            responseObject.setMessage(Constants.CUSTOMER_ADDED);
            responseObject.setStatus(true);
            customer.setEnable(true);
            customer.setLastModifiedDate(CustomerUtil.getCurrentTimestamp());
            customer.setCreateDate(CustomerUtil.getCurrentTimestamp());
            kafkaTemplate.send(topicName, customer);
        } catch (Exception e) {
            responseObject.setMessage(Constants.INTERNAL_SERVER_ERROR);
            LOGGER.error("Failed to Add", e);
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    private CustomerResponseObject getCustomerUpdateOrAddResponseObject(Customer customer, Optional<Customer> customerOptional) {
        CustomerResponseObject responseObject = new CustomerResponseObject();

        try {
            Customer existingCustomer = customerOptional.get();
            if (customer.getName().isEmpty() || customer.getEmail().isEmpty() || customer.getPhoneNo().isEmpty() || String.valueOf(customer.getClientId()).isEmpty()) {
                responseObject.setMessage("Enter all fields");
                return responseObject;
            }

            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                if (!CustomerUtil.checkCustomerEmail(customer)) {
                    responseObject.setMessage("Invalid email address");
                    return responseObject;
                }
                existingCustomer.setEmail(customer.getEmail());
            }

            if (customer.getName() != null && !customer.getName().isEmpty()) {
                existingCustomer.setName(customer.getName());
            }

            if (customer.getPhoneNo() != null && !customer.getPhoneNo().isEmpty()) {
                if (!CustomerUtil.checkCustomerPhoneNo(customer)) {
                    responseObject.setMessage("Invalid phone number");
                    return responseObject;
                }
                existingCustomer.setPhoneNo(customer.getPhoneNo());
            }

            if (String.valueOf(customer.getClientId()) != null && !String.valueOf(customer.getClientId()).isEmpty()) {
                existingCustomer.setClientId(customer.getClientId());
            }

            existingCustomer.setLastModifiedDate(CustomerUtil.getCurrentTimestamp());
            CustomerResponseObject.CustomerDetails customerDetails = new CustomerResponseObject.CustomerDetails();
            customerDetails.setCustomerId(existingCustomer.getId());
            customerDetails.setCustomerCode(existingCustomer.getCustomerCode());
            responseObject.setMessage(Constants.UPDATED);
            responseObject.setStatus(true);
            customerDetails.setCustomerName(existingCustomer.getName());
            customerDetails.setCustomerEmail(existingCustomer.getEmail());

            responseObject.setCustomerDetails(customerDetails);
            kafkaTemplate.send(topicName, existingCustomer);
        } catch (Exception e) {
            responseObject.setMessage(Constants.FAILED_TO_UPDATE);
            LOGGER.error("Failed to update", e);
            return null;

        }
        return responseObject;
    }


    public String saveOrUpdateExcelDataToDb(MultipartFile file) {
        InputStream inputStream = null;
        Workbook workbook = null;
        if (!file.getResource().getFilename().contains("xlsx")) {
            return "Please give Excel file only";
        }
        try {
            inputStream = file.getInputStream();
            workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Customer customer = new Customer();
                customer.setCustomerCode(CustomerUtil.generateCustomerCode());
                customer.setClientId((int) row.getCell(0).getNumericCellValue());
                customer.setName(row.getCell(1).getStringCellValue());
                customer.setPhoneNo(row.getCell(2).getStringCellValue());
                customer.setEmail(row.getCell(3).getStringCellValue());
                customerDao.addCustomer(customer);
            }
            inputStream.close();
            workbook.close();
            return "Excel data uploaded";
        } catch (Exception e) {
            return "Error" + e.getMessage();
        }
    }
}
