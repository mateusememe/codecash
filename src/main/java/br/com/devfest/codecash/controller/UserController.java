package br.com.devfest.codecash.controller;

import br.com.devfest.codecash.dto.CreateUserInput;
import br.com.devfest.codecash.dto.UpdateUserInput;
import br.com.devfest.codecash.model.User;
import br.com.devfest.codecash.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Mapeia para a 'Query' userById no schema.
    @QueryMapping
    public User userById(@Argument UUID id) {
        try {
            return userService.getUserById(id);
        } catch (Exception e) {
            log.error("error occurred: {}", e.getLocalizedMessage());
            return null;
        }
    }

    // Mapeia para a 'Query' allUsers no schema.
    @QueryMapping
    public List<User> allUsers() {
        return userService.getAllUsers();
    }

    // Mapeia para a 'Mutation' createUser no schema.
    // @Valid ativa as validações que definimos no DTO.
    @MutationMapping
    public User createUser(@Argument @Valid CreateUserInput input) {
        return userService.createUser(input);
    }

    // NOVO MÉTODO: Mapeia para a mutation 'updateUser'
    @MutationMapping
    public User updateUser(@Argument UUID id, @Argument @Valid UpdateUserInput input) {
        return userService.updateUser(id, input);
    }

    // NOVO MÉTODO: Mapeia para a mutation 'deleteUser'
    @MutationMapping
    public boolean deleteUser(@Argument UUID id) {
        userService.deleteUser(id);
        // Se o serviço não lançar uma exceção, a operação foi bem-sucedida.
        // Retornamos 'true' para cumprir o contrato do schema GraphQL.
        return true;
    }
}