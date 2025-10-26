package br.com.devfest.codecash.repository;

import br.com.devfest.codecash.model.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailOrDocument(String email, String document);
}
