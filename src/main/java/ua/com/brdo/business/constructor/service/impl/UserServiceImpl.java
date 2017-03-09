package ua.com.brdo.business.constructor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ua.com.brdo.business.constructor.constraint.UniqueValidatable;
import ua.com.brdo.business.constructor.model.Role;
import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.repository.RoleRepository;
import ua.com.brdo.business.constructor.repository.UserRepository;
import ua.com.brdo.business.constructor.service.NotFoundException;
import ua.com.brdo.business.constructor.service.UserService;

import static java.util.Objects.nonNull;

@Service
public class UserServiceImpl implements UserService, UserDetailsService, UniqueValidatable {

    private static final String ROLE_USER = "ROLE_USER";
    private static final int TOKEN_EXPIRE_IN_DAYS = 1;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserRepository userRepo, final RoleRepository roleRepo, final PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Transactional
    @Override
    public User create(User user) {
        final Set<Role> authorities = user.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            final Role userRole = roleRepo.findByTitle(ROLE_USER)
                    .orElseThrow(() -> new NotFoundException("The role was not found."));
            user.setAuthorities(Collections.singleton(userRole));
        }
        encodePassword(user);
        return userRepo.saveAndFlush(user);
    }

    private void encodePassword(User user) {
        final char[] rawPassword = user.getRawPassword();
        final CharBuffer buffer = CharBuffer.wrap(rawPassword);
        final String encodedPassword = passwordEncoder.encode(buffer);
        user.setPassword(encodedPassword);
        buffer.clear();
        Arrays.fill(rawPassword, 'x');
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with given user was not found."));
    }

    @Override
    public User findByToken(String token) {
        return userRepo.findByToken(token).orElseThrow(() -> new NotFoundException("Token not found. It may have expired."));
    }

    public boolean isAvailable(String fieldName, String fieldValue, Long id) {
        switch (fieldName) {
            case "username":
                return isUsernameAvailable(fieldValue, id);
            case "email":
                return isEmailAvailable(fieldValue, id);
            default:
                throw new IllegalArgumentException("Unexpected field was passed to isAvailable method.");
        }
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return nonNull(email) && userRepo.emailAvailable(email);
    }

    @Override
    public boolean isEmailAvailable(String email, Long id) {
        return nonNull(email) && userRepo.emailAvailable(email, id);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return nonNull(username) && userRepo.usernameAvailable(username);
    }

    @Override
    public boolean isUsernameAvailable(String username, Long id) {
        return nonNull(username) && userRepo.usernameAvailable(username, id);
    }

    @Override
    public User update(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getAuthorities());
        return userRepo.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void delete(final long id) {
        userRepo.delete(id);
    }

    @Override
    @Transactional
    public void delete(final User user) {
        userRepo.delete(user);
    }

    @Transactional
    @Scheduled(cron = "${cron.delete.expired.token}")
    public void deleteExpiredTokens() {
        LocalDateTime expireTimestamp = LocalDateTime.now().minusDays(TOKEN_EXPIRE_IN_DAYS);
        userRepo.deleteNotVerifiedExpiredUsers(expireTimestamp);
    }
}
