package br.com.devfest.codecash.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionInput(
    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor da transação deve ser positivo")
    BigDecimal amount,

    @NotNull(message = "O ID da conta pagadora não pode ser nulo")
    UUID payerAccountId,

    @NotNull(message = "O ID da conta recebedora não pode ser nulo")
    UUID payeeAccountId
) {
}