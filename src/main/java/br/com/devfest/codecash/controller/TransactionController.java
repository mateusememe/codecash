package br.com.devfest.codecash.controller;

import br.com.devfest.codecash.dto.AddFundsInput;
import br.com.devfest.codecash.dto.CreateTransactionInput;
import br.com.devfest.codecash.model.Account;
import br.com.devfest.codecash.model.Transaction;
import br.com.devfest.codecash.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Mapeia para a 'Mutation' createTransaction no schema.
    @MutationMapping
    public Transaction createTransaction(@Argument @Valid CreateTransactionInput input) {
        return transactionService.createTransaction(input);
    }

    @MutationMapping
    public Account addFunds(@Argument @Valid AddFundsInput input) {
        return transactionService.addFunds(input);
    }
}

