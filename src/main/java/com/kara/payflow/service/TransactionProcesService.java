package com.kara.payflow.service;

import com.kara.payflow.model.TransactionCsvRow;

import java.util.List;

public interface TransactionProcesService {

    void process(List<TransactionCsvRow> transactionCsvRows);
}
