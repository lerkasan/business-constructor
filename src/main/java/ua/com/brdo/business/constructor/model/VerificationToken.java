package ua.com.brdo.business.constructor.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "verification_token")
@Data
@EqualsAndHashCode(of = {"token"})
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 64)
    private String token = UUID.randomUUID().toString();

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "expiry_timestamp")
    private LocalDateTime expiryTimeStamp = LocalDateTime.now().plusDays(1);

    public VerificationToken(User user) {
        this.user = user;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTimeStamp);
    }
}