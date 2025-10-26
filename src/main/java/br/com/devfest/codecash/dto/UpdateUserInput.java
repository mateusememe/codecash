package br.com.devfest.codecash.dto;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

/**
 * DTO para a atualização de dados do usuário.
 * Os campos não têm @NotBlank para permitir atualizações parciais
 * (atualizar apenas o nome ou apenas o e-mail).
 * A lógica de validação mais complexa (como verificar se pelo menos um campo foi enviado)
 * pode ser feita na camada de serviço, se necessário.
 */
public record UpdateUserInput(
    @Length(min = 2, message = "O nome deve ter pelo menos 2 caracteres")
    String name,

    @Email(message = "Formato de e-mail inválido")
    String email
) {
}