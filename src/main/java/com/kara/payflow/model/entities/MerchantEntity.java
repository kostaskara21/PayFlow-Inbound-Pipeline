package com.kara.payflow.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "merchants", schema = "public")
@Data
public class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_code", nullable = false, unique = true, length = 64)
    private String merchantCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "country", length = 64)
    private String country;

    @OneToMany(mappedBy = "merchant")
    private List<TransactionEntity> transactionEntities;

}
