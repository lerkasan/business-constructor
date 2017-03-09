package ua.com.brdo.business.constructor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.com.brdo.business.constructor.constraint.Ascii;
import ua.com.brdo.business.constructor.constraint.Unique;
import ua.com.brdo.business.constructor.service.impl.UserServiceImpl;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(of = {"username", "email", "creationDate"})
@Validated
@JsonInclude(NON_NULL)
@Unique.List(value = {
    @Unique(field = "username", service = UserServiceImpl.class, message = "Цей логін вже зайнятий."),
    @Unique(field = "email", service = UserServiceImpl.class, message = "Користувач з такою email адресою вже існує.")
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;

    @NotEmpty(message = "E-mail field is required.")
    @Email(regexp = ".+@.+\\..+", message = "Incorrect format of e-mail.")
    @Column(unique = true, nullable = false)
    private String email;

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @NotNull(message = "Password field is required.")
    @Size(min = 8, max = 32, message = "Password length must be between 8 and 32 characters.")
    @Ascii(message = "Password can include upper and lower case latin letters, numerals (0-9) and special symbols.")
    private char[] rawPassword;

    @JsonIgnore
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Column(name = "creation_timestamp", nullable = false, updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime creationTimestamp = LocalDateTime.now();

    @JsonIgnore
    @Column(length = 64)
    private String token = UUID.randomUUID().toString();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonProperty("roles")
    private Set<Role> authorities;

    private boolean enabled = false;

    public User() {
        authorities = new HashSet<>();
    }

    public boolean grantAuthorities(Role role) {
        return authorities.add(role);
    }

    public boolean revokeAuthorities(Role role) {
        return authorities.remove(role);
    }

    @Override
    public Set<Role> getAuthorities() {
        return Collections.unmodifiableSet(authorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
