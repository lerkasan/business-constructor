package ua.com.brdo.business.constructor.service;

import java.util.List;

import ua.com.brdo.business.constructor.model.User;

public interface UserService {

    User create(User user);

    List<User> findAll();

    User findByToken(String token);

    User update(User user);

    void delete(final long id);

    void delete(User user);

    boolean isEmailAvailable(String email);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email, Long id);

    boolean isUsernameAvailable(String username, Long id);
}
