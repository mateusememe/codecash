package br.com.devfest.codecash.service;

import br.com.devfest.codecash.dto.AddFundsInput;
import br.com.devfest.codecash.dto.CreateTransactionInput;
import br.com.devfest.codecash.exception.AccountNotFoundException;
import br.com.devfest.codecash.exception.InsufficientFundsException;
import br.com.devfest.codecash.model.Account;
import br.com.devfest.codecash.model.Transaction;
import br.com.devfest.codecash.repository.AccountRepository;
import br.com.devfest.codecash.repository.TransactionRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction createTransaction(CreateTransactionInput input) {
        Account payer = accountRepository.findById(input.payerAccountId())
            .orElseThrow(() -> new AccountNotFoundException("payer account not found"));

        Account receiver = accountRepository.findById(input.payeeAccountId())
            .orElseThrow(() -> new AccountNotFoundException("recipient account not found"));

        if (payer.getBalance().compareTo(input.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        if (payer.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("payer's account cannot be the same as the recipient's");
        }

        payer.setBalance(payer.getBalance().subtract(input.amount()));
        receiver.setBalance(receiver.getBalance().add(input.amount()));

        accountRepository.saveAll(List.of(payer, receiver));


        Transaction transaction = Transaction.builder()
            .amount(input.amount())
            .payerAccount(payer)
            .payeeAccount(receiver)
            .transactionTime(Instant.now())
            .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Account addFunds(AddFundsInput input) {
        Account account = accountRepository.findById(input.accountId())
            .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + input.accountId()));

        account.setBalance(account.getBalance().add(input.amount()));

        return accountRepository.save(account);
    }
}