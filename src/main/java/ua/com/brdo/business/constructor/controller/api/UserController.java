package ua.com.brdo.business.constructor.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.model.VerificationToken;
import ua.com.brdo.business.constructor.service.UserService;
import ua.com.brdo.business.constructor.utils.HtmlRender;
import ua.com.brdo.business.constructor.utils.Mailer;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HtmlRender htmlRender;
    private final Mailer mailer;

    @Autowired
    public UserController(final UserService userService, HtmlRender htmlRender, Mailer mailer) {
        this.userService = userService;
        this.htmlRender = htmlRender;
        this.mailer = mailer;
    }

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody final User user) {
        final User registeredUser = userService.create(user);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(registeredUser);
    }

    @PostMapping("register")
    public ResponseEntity registerUser(@Valid @RequestBody final User user, HttpServletRequest request) {
        User registeredUser = userService.create(user);
        String userEmail = registeredUser.getEmail();
        String requestUrl = request.getRequestURL().toString();
        VerificationToken verificationToken = userService.generateAndSaveVerificationToken(registeredUser);
        String verificationUrl = requestUrl + "?verification=" + verificationToken.getToken();
        String message = htmlRender.renderVerificationEmail(registeredUser, verificationUrl);
        mailer.send(userEmail, "Підтвердження реєстрації на сайті", message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("available")
    public boolean isEmailAvailable(@RequestParam String email) {
        return userService.isEmailAvailable(email);
    }

    @GetMapping
    public List<User> getUsers() { return userService.findAll();}

    @GetMapping("current")
    public ResponseEntity getCurrentUser(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
           return ResponseEntity.notFound().build();
        }
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(currentUser);
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity updateUser(@PathVariable long userId, @RequestBody User user) {
        user.setId(userId);
        User updatedUser = userService.update(user);
        return ResponseEntity.ok().body(updatedUser);
    }
}
