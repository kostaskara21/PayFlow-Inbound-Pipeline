package com.kara.payflow.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "customers", schema = "public")
@Data
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", nullable = false, unique = true, length = 64)
    private String customerCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", length = 320)
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<TransactionEntity> transactionEntities;



}
