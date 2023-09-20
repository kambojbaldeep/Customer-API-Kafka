package com.kapture.customer.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "customer", indexes = {@Index(name = "email", columnList = "email"), @Index(name = "customerCode", columnList = "customer_code")})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
    private int id;

    @Column(name = "client_id", nullable = false)
    private int clientId;

    @Column(name = "name")
    private String name;

    @Column(name = "last_modified_Date")
    private Timestamp lastModifiedDate;

    @Column(name = "create_date")
    private Timestamp createDate;

    @Column(name = "phone_no", unique = true)
    private String phoneNo;

    @Column(name = "customer_code", unique = true)
    private String customerCode;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "enable")
    private boolean enable;







//    @Column(name = "client_id", nullable = false)
//    private int clientId;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "last_modified_Date")
//    private Timestamp lastModifiedDate;
//
//    @Column(name = "create_date")
//    private Timestamp createDate;
//
//    @Column(name = "phone_no", nullable = false, unique = true)
//    private String phoneNo;
//
//    @Column(name = "customer_code", nullable = false, unique = true)
//    private String customerCode;
//
//    @Column(name = "email", nullable = false, unique = true)
//    private String email;
//
//    @Column(name = "enable", nullable = false)
//    private boolean enable;
}
