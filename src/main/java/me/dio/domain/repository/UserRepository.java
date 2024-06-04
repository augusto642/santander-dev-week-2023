package me.dio.domain.repository;

import me.dio.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByNameContaining(String name, Pageable pageable);
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByCardNumber(String cardNumber);

    default Page<User> findByNameContaining(String name, PageRequest of) {
        return null;
    }
}
