package br.com.devfest.codecash.service;

import br.com.devfest.codecash.dto.CreateUserInput;
import br.com.devfest.codecash.dto.UpdateUserInput;
import br.com.devfest.codecash.exception.UserAlreadyExistsException;
import br.com.devfest.codecash.model.Account;
import br.com.devfest.codecash.model.User;
import br.com.devfest.codecash.repository.UserRepository;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(CreateUserInput input) {
        if (userRepository.existsByEmailOrDocument(input.email(), input.document())) {
            throw new UserAlreadyExistsException("user with this email or document already exists");
        }

        User newUser = User.builder()
            .name(input.name())
            .email(input.email())
            .document(input.document())
            .password(input.password())
            .createdAt(Instant.now())
            .build();

        Account newAccount = Account.builder()
            .user(newUser)
            .balance(BigDecimal.ZERO)
            .build();

        newUser.setAccount(newAccount);

        return userRepository.save(newUser);
    }

    /**
     * Busca um usuário pelo ID. O resultado desta busca será cacheado.
     * @Cacheable: Anotação que instrui o Spring a guardar o retorno deste método em cache.
     * - value = "users": É o nome do "espaço" de cache. Podemos ter vários caches (ex: "users", "products").
     * - key = "#id": É a chave única para este item no cache. Usamos Spring Expression Language (SpEL)
     * para dizer que a chave é o próprio argumento 'id' do método.
     */
    @Cacheable(value = "users", key = "#id")
    public User getUserById(UUID id) {
        // Adicione este log para ver o cache em ação!
        // Ele só aparecerá na primeira vez que você chamar este método para um determinado ID.
        try {
            log.info("searching user by id" + id);
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("error occurred: {}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Deleta um usuário pelo seu ID.
     * @CacheEvict: Anotação que instrui o Spring a remover uma entrada do cache.
     * - value = "users": Especifica o mesmo "espaço" de cache do @Cacheable.
     * - key = "#id": Aponta para a chave exata que deve ser removida. É fundamental
     * que a chave aqui seja construída da mesma forma que no @Cacheable para que
     * o Spring encontre e remova a entrada correta.
     */
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(UUID id) {
        log.info("### DELETANDO USUÁRIO E REMOVENDO DO CACHE ### ID: {}", id);
        // TODO: check if user exists here before delete
        userRepository.deleteById(id);
    }

    /**
     * Atualiza os dados de um usuário.
     * @CachePut: Garante que o método sempre execute e que o valor retornado
     * seja colocado no cache, sobrescrevendo o valor antigo.
     * - value = "users": O mesmo espaço de cache.
     * - key = "#id": A chave do usuário que está sendo atualizado.
     *
     * IMPORTANTE: O método anotado com @CachePut DEVE retornar o objeto
     * que será colocado no cache. Neste caso, o User atualizado.
     */
    @CachePut(value = "users", key = "#id")
    public User updateUser(UUID id, UpdateUserInput input) {
        log.info("### ATUALIZANDO USUÁRIO NO BANCO E NO CACHE ### ID: {}", id);
        User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        if (input.name() != null && !input.name().isBlank()) {
            userToUpdate.setName(input.name());
        }
        if (input.email() != null && !input.email().isBlank()) {
            userToUpdate.setEmail(input.email());
        }

        return userRepository.save(userToUpdate);
    }

    // Método para listar todos os usuários.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}