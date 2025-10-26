package br.com.devfest.codecash.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal amount;

    // --- ANOTAÇÕES DE RELACIONAMENTO ---
    // Define um relacionamento muitos-para-um: muitas transações podem ter a mesma conta como pagadora.
    // @JoinColumn especifica a chave estrangeira 'payer_account_id' nesta tabela.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_account_id", nullable = false)
    private Account payerAccount;

    // Define outro relacionamento muitos-para-um: muitas transações podem ter a mesma conta como recebedora.
    // @JoinColumn especifica a chave estrangeira 'payee_account_id' nesta tabela.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_account_id", nullable = false)
    private Account payeeAccount;

    @Column(name = "transaction_time", nullable = false)
    private Instant transactionTime;
}