package me.dio.service.impl;

import me.dio.domain.model.User;
import me.dio.domain.repository.UserRepository;
import me.dio.service.EmailService;
import me.dio.service.UserService;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Long UNCHANGEABLE_USER_ID = 1L;

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<User> findByName(String name, int page, int size) {
        return this.userRepository.findByNameContaining(name, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByAccountNumber(String accountNumber) {
        return this.userRepository.findByAccountNumber(accountNumber);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByCardNumber(String cardNumber) {
        return this.userRepository.findByCardNumber(cardNumber);
    }

    @Transactional
    public User create(User userToCreate) {
        validateUserForCreation(userToCreate);
        notifyUserCreation(userToCreate);
        return this.userRepository.save(userToCreate);
    }

    @Transactional
    public User update(Long id, User userToUpdate) {
        validateUserForUpdate(id, userToUpdate);
        User dbUser = findById(id);
        dbUser.setName(userToUpdate.getName());
        dbUser.setAccount(userToUpdate.getAccount());
        dbUser.setCard(userToUpdate.getCard());
        dbUser.setFeatures(userToUpdate.getFeatures());
        dbUser.setNews(userToUpdate.getNews());
        notifyUserUpdate(dbUser);
        return this.userRepository.save(dbUser);
    }

    @Transactional
    public void delete(Long id) {
        validateChangeableId(id, "deleted");
        User dbUser = findById(id);
        this.userRepository.delete(dbUser);
        notifyUserDeletion(dbUser);
    }

    private void validateUserForCreation(User userToCreate) {
        Optional.ofNullable(userToCreate).orElseThrow(() -> new BusinessException("User to create must not be null."));
        // Perform additional validations...
    }

    private void validateUserForUpdate(Long id, User userToUpdate) {
        validateChangeableId(id, "updated");
        findById(id); // Ensure user exists
        // Perform additional validations...
    }

    private void notifyUserCreation(User user) {
        emailService.sendEmail(user.getEmail(), "Welcome!", "Your account has been created successfully.");
    }

    private void notifyUserUpdate(User user) {
        emailService.sendEmail(user.getEmail(), "Account Update", "Your account details have been updated.");
    }

    private void notifyUserDeletion(User user) {
        emailService.sendEmail(user.getEmail(), "Account Deleted", "Your account has been deleted from the system.");
    }

    private void validateChangeableId(Long id, String operation) {
        if (UNCHANGEABLE_USER_ID.equals(id)) {
            throw new BusinessException("User with ID " + UNCHANGEABLE_USER_ID + " cannot be " + operation + ".");
        }
    }
}
