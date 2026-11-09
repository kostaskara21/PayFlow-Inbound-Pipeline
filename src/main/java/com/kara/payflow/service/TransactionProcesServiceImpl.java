package com.kara.payflow.service;

import com.example.payflow.contracts.Adjustment;
import com.example.payflow.contracts.Transaction;
import com.example.payflow.contracts.TransactionStatusHistory;
import com.kara.payflow.mapper.TransactionMapper;
import com.kara.payflow.model.TransactionCsvRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionProcesServiceImpl implements TransactionProcesService {

    private  final  TransactionMapper transactionMapper;

    @Override
    public void process(List<TransactionCsvRow> transactionCsvRow) {

        List<Transaction> transactions = new ArrayList<>();

        for(TransactionCsvRow row: transactionCsvRow){
            Transaction transaction = null;

            for(Transaction lisTr : transactions){
                if (lisTr.getTransactionId().equals(row.transactionId())) {
                    transaction = lisTr;
                    break;
                }
            }

            if (transaction  == null) {
                transaction = transactionMapper.ToTransaction(row);
                //Adjustment is a list so we need to create it since contract does not intialize it (if not NPE)
                transaction.setAdjustments(new ArrayList<>());
                transaction.setStatusHistory(new ArrayList<>());
                transactions.add(transaction);
            }

            Adjustment adjustment = transactionMapper.ToAdjustment(row);
            TransactionStatusHistory statusHistory = transactionMapper.ToStatusHistory(row);

            transaction.getAdjustments().add(adjustment);
            transaction.getStatusHistory().add(statusHistory);
        }

        log.info("Processed {} transactions: {}", transactions.size(), transactions);
    }
}
