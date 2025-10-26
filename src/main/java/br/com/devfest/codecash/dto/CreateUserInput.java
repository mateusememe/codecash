package br.com.devfest.codecash.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Usamos Records para DTOs imutáveis.
// As anotações de validação garantem a integridade dos dados de entrada.
public record CreateUserInput(
    @NotBlank(message = "O nome não pode estar em branco")
    String name,

    @NotBlank(message = "O e-mail não pode estar em branco")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @NotBlank(message = "O documento não pode estar em branco")
    String document,

    @NotBlank(message = "A senha не pode estar em branco")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    String password
) {
}