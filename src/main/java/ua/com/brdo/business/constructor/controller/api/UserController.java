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

import javax.validation.Valid;

import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
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
