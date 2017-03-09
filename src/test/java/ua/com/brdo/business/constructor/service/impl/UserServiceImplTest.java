package ua.com.brdo.business.constructor.service.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import ua.com.brdo.business.constructor.model.Role;
import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.repository.RoleRepository;
import ua.com.brdo.business.constructor.repository.UserRepository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final Role ROLE_USER = new Role(1L, "ROLE_USER");

    @Mock private UserRepository userRepo;
    @Mock private RoleRepository roleRepo;
    @Mock private PasswordEncoder passwordEncoder;
    private UserServiceImpl underTest;

    @Before
    public void setUp() {
        this.underTest = new UserServiceImpl(userRepo, roleRepo, passwordEncoder);
    }

    @Test
    public void shouldDelegateToUserRepoOnFindAll() throws Exception {
        underTest.findAll();

        verify(userRepo, times(1)).findAll();
    }

    @Test
    public void shouldCreateUserWithGivenUserDetails() throws Exception {
        final User user = createUser();
        when(userRepo.saveAndFlush(user)).thenReturn(user);

        final User result = underTest.create(user);

        assertThat(result, is(user));
    }

    @Test
    public void shouldCreateUserWithRoleUserIfThereAreNoAny() throws Exception {
        final User user = createUser();
        user.setAuthorities(new HashSet<>());
        when(userRepo.saveAndFlush(user)).thenReturn(user);
        when(roleRepo.findByTitle(ROLE_USER.getTitle())).thenReturn(Optional.of(ROLE_USER));

        final User result = underTest.create(user);
        final Set<Role> resultAuthorities = result.getAuthorities();

        assertThat(resultAuthorities.size(), is(1));
        assertThat(resultAuthorities, contains(ROLE_USER));
    }

    @Ignore("Currently implementation of User class doesn't allow authorities to be null.")
    @Test
    public void shouldCreateUserWithRoleUserIfAuthoritiesAreNull() throws Exception {
        final User user = createUser();
        user.setAuthorities(null);
        when(userRepo.saveAndFlush(user)).thenReturn(user);
        when(roleRepo.findByTitle(ROLE_USER.getTitle())).thenReturn(Optional.of(ROLE_USER));

        final User result = underTest.create(user);
        final Set<Role> resultAuthorities = result.getAuthorities();

        assertThat(resultAuthorities.size(), is(1));
        assertThat(resultAuthorities, contains(ROLE_USER));
    }

    @Test
    public void shouldEncodePasswordOnCreateUser() throws Exception {
        final User user = createUser();
        final String encodedPassword = "encodedPassword";
        final char[] expectedRawPassword = new char[user.getRawPassword().length];
        Arrays.fill(expectedRawPassword, 'x');
        when(userRepo.saveAndFlush(user)).thenReturn(user);
        when(passwordEncoder.encode((CharSequence) notNull())).thenReturn(encodedPassword);

        final User result = underTest.create(user);

        assertThat(result.getPassword(), equalTo(encodedPassword));
        assertThat(result.getRawPassword(), equalTo(expectedRawPassword));
    }

    @Test
    public void shouldReturnTrueIfEmailIsAvailable() throws Exception {
        final String email = "dummyEmail";
        when(userRepo.emailAvailable(email)).thenReturn(true);

        final boolean result = underTest.isEmailAvailable(email);

        assertThat(result, is(true));
    }

    @Test
    public void shouldReturnFalseIfEmailIsNotAvailable() throws Exception {
        final String email = "dummyEmail";
        when(userRepo.emailAvailable(email)).thenReturn(false);

        final boolean result = underTest.isEmailAvailable(email);

        assertThat(result, is(false));
    }

    @Test
    public void shouldReturnFalseIfEmailIsNull() throws Exception {
        final String email = null;

        final boolean result = underTest.isEmailAvailable(email);

        assertThat(result, is(false));
    }

    @Test
    public void shouldReturnTrueIfUsernameIsAvailable() throws Exception {
        final String username = "dummyUsername";
        when(userRepo.usernameAvailable(username)).thenReturn(true);

        final boolean result = underTest.isUsernameAvailable(username);

        assertThat(result, is(true));
    }

    @Test
    public void shouldReturnFalseIfUsernameIsNotAvailable() throws Exception {
        final String username = "dummyUsername";
        when(userRepo.usernameAvailable(username)).thenReturn(false);

        final boolean result = underTest.isUsernameAvailable(username);

        assertThat(result, is(false));
    }

    @Test
    public void shouldReturnFalseIfUsernameIsNull() throws Exception {
        final String username = null;

        final boolean result = underTest.isUsernameAvailable(username);

        assertThat(result, is(false));
    }

    @Test
    public void shouldSuccessfullyLoadUserDetails() throws Exception {
        final String username = "dummyUsername";
        final User user = createUser(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        final UserDetails result = underTest.loadUserByUsername(username);

        assertThat(result, is(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowExceptionOnLoadUserByUsernameIfUserWasNotFound() throws Exception {
        final String username = "dummyUsername";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        underTest.loadUserByUsername(username);
    }

    private User createUser() {
        return createUser("dummyUsername");
    }

    private User createUser(final String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail("dummy@email.com");
        user.setPassword("dummyPassword");
        user.setRawPassword("dummyPassword".toCharArray());
        user.setAuthorities(Collections.singleton(ROLE_USER));
        return user;
    }
}
