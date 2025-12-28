package com.resumeanalyzer.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.auth.exceptions.InvalidEmailFormatException;
import com.resumeanalyzer.common.utils.AuthServiceUtils;
import com.resumeanalyzer.common.utils.JwtUtils;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private AuthDAO dao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ActivityService activityService;

    private AuthService service;

    private AuthServiceUtils serviceUtils;

    @BeforeEach
    public void setup() {
        serviceUtils = new AuthServiceUtils();
        service = new AuthService(dao, serviceUtils, passwordEncoder, jwtUtils, activityService);
    }

    @Test
    public void testSignup_withValidUser_returnsUserId() {

        User user = new User("test_user", "username", "valid@gmail.com", "plain_password", "company", "role");

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUserId()).thenReturn(10);
        when(dao.signUp(user)).thenReturn(mockUser);

        when(passwordEncoder.encode(user.getPasswordHash())).thenReturn("hashed_password");

        int returnedId = service.signup(user);
        Assertions.assertEquals(10, returnedId);

        verify(passwordEncoder, times(1)).encode("plain_password");
        verify(dao, times(1)).signUp(user);
        

    }

    @Test
    public void testSignup_invalidEmail_throwsException() {

        User user = new User("test_user", "username", "invalid_email", "plain_password", "company", "role");
        when(passwordEncoder.encode(user.getPasswordHash())).thenReturn("hashed_password");
        Assertions.assertThrowsExactly(InvalidEmailFormatException.class, () -> service.signup(user));
        verify(dao, never()).signUp(any());
    }

}
