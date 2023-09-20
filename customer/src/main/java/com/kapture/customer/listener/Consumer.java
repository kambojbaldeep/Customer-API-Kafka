package com.kapture.customer.listener;

import com.kapture.customer.dao.CustomerDao;
import com.kapture.customer.model.Customer;
import com.kapture.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    private CustomerDao customerDao;

    @Value("${spring.kafka.test.topic}")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.test.topic}", groupId = "customer-group", containerFactory = "kafkaListenerContainerFactory")

    public void consume(Customer customer) {
        try {
            customerDao.addCustomer(customer);
            System.out.println("Data Consumed");
            LOGGER.info("Data Consumed");
        }catch (Exception e){
            LOGGER.error("Internal server error",e);
        }
    }
}
