package br.com.devfest.codecash.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record AddFundsInput(
    @NotNull(message = "O ID da conta não pode ser nulo")
    UUID accountId,

    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor a ser adicionado deve ser positivo")
    BigDecimal amount
) {
}
