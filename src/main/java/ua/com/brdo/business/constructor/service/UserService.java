package ua.com.brdo.business.constructor.service;

import java.util.List;
import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.model.VerificationToken;

public interface UserService {

    User create(User user);

    List<User> findAll();

    User update(User user);

    boolean isEmailAvailable(String email);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email, Long id);

    boolean isUsernameAvailable(String username, Long id);

    VerificationToken generateAndSaveVerificationToken(User user);
}
