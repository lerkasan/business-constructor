package ua.com.brdo.business.constructor.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ua.com.brdo.business.constructor.constraint.EmailAddress;
import ua.com.brdo.business.constructor.constraint.Unique;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(of = {"username", "email", "creationDate"})
@JsonInclude(NON_NULL)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    @Unique(type = "username", message = "User with this username is already registered. Try another username.")
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;

    @NotEmpty(message = "E-mail field is required.")
    @EmailAddress(message = "Incorrect format of e-mail.")
    @Unique(type = "email", message = "User with this e-mail is already registered. Try another e-mail.")
    @Column(unique = true, nullable = false)
    private String email;

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    @NotEmpty(message = "Password field is required.")
    @Size(min = 8, max = 32, message = "Password length must be between 8 and 32 characters.")
    @Pattern(regexp = "^[!-~]{8,32}$", message = "Password could include upper and lower case latin letters, numerals (0-9) and special symbols.")
    private String rawPassword;

    @JsonIgnore
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate creationDate = LocalDate.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities;

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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
        return true;
    }
}
