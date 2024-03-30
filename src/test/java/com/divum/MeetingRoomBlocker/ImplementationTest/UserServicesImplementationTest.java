package com.divum.MeetingRoomBlocker.ImplementationTest;

import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.Implementation.UserServicesImplementation;
import com.divum.MeetingRoomBlocker.Service.UserServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserServicesImplementationTest {
    @Mock
    private UserEntityRepository userEntityRepository;
    @InjectMocks
    private UserServicesImplementation userServicesImplementation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_Sucess() {
        UserEntity user = new UserEntity();
        user.setEmail("user@divum.in");
        user.setId(1l);
        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.of(user));
        UserDetails userDetails = userServicesImplementation.loadUserByUsername("user@divum.in");
        assertNotNull(userDetails);
    }

    @Test
    void loadUserByUsername_USER_NOT_FOUND() {
        when(userEntityRepository.findByEmail("user@divum.in")).thenReturn(Optional.empty());
        UserServices userServices = new UserServicesImplementation(userEntityRepository);
        assertThrows(UsernameNotFoundException.class, () -> {
            userServices.loadUserByUsername("user1@divum.in");
        }, Constants.USER_NOT_FOUND);
    }
}