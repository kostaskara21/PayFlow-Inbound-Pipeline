package com.kara.payflow.mapper;

import com.example.payflow.contracts.*;
import com.kara.payflow.model.TransactionCsvRow;
import org.mapstruct.*;

@Mapper
public interface TransactionMapper {


    @Mappings({
            @Mapping(source = "transactionId", target = "transactionId"),
            @Mapping(source = "transactionType", target = "transactionType"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "currency", target = "currency"),
            @Mapping(target = "customer", source = "source"),
            @Mapping(target = "merchant", source = "source"),
            @Mapping(target = "payment", source = "source"),
            @Mapping(target = "adjustments", ignore = true)

    })
    Transaction ToTransaction(TransactionCsvRow source);


    @Mappings({
            @Mapping(source = "merchantCode", target = "merchantCode"),
            @Mapping(source = "merchantName", target = "name"),
            @Mapping(source = "merchantCountry", target = "country"),

    })
    Merchant ToMerchant(TransactionCsvRow source);

    @Mappings({
            @Mapping(source = "customerCode", target = "customerCode"),
            @Mapping(source = "customerName", target = "name"),
            @Mapping(source = "customerEmail", target = "email"),

    })
    Customer ToCustomer(TransactionCsvRow source);

    @Mappings({
            @Mapping(source = "cardBrand", target = "cardBrand"),
            @Mapping(source = "paymentMethod", target = "method"),
            @Mapping(source = "cardLast4", target = "cardLast4"),
            @Mapping(source = "authorizationCode", target = "authorizationCode"),

    })
    Payment ToPayment(TransactionCsvRow source);

    @Mappings({
            @Mapping(source = "adjustmentType", target = "type"),
            @Mapping(source = "adjustmentAmount", target = "amount"),
            @Mapping(source = "adjustmentCurrency", target = "currency")

    })
    Adjustment ToAdjustment(TransactionCsvRow source);


    @Mappings({
            @Mapping(source = "statusHistoryStatus", target = "status"),
            @Mapping(source = "statusHistoryTimestamp", target = "timestamp")
    })
    TransactionStatusHistory ToStatusHistory(TransactionCsvRow source);

}