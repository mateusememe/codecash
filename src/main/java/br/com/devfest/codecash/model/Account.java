package br.com.devfest.codecash.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal balance;

    // --- ANOTAÇÃO DE RELACIONAMENTO ---
    // Define a parte "dona" do relacionamento um-para-um.
    // @JoinColumn especifica a coluna de chave estrangeira (user_id) nesta tabela ('accounts')
    // que se conecta à chave primária da tabela 'users'.
    @ToString.Exclude
    @JsonBackReference // ANOTAÇÃO ADICIONADA: Este é o lado "filho". Não será serializado.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}