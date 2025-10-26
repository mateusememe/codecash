package br.com.devfest.codecash.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String document;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // --- ANOTAÇÃO DE RELACIONAMENTO ---
    // Define um relacionamento um-para-um.
    // 'mappedBy = "user"' indica que a entidade Account é a "dona" deste relacionamento.
    // Isso significa que a coluna da chave estrangeira (user_id) está na tabela 'accounts'.
    // 'cascade = CascadeType.ALL' faz com que operações (salvar, deletar) em User se propaguem para a Account associada.
    @ToString.Exclude
    @JsonManagedReference // ANOTAÇÃO ADICIONADA: Este é o lado "pai".
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Account account;
}