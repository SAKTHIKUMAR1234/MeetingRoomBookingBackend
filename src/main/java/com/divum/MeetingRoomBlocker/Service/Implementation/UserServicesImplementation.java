package com.divum.MeetingRoomBlocker.Service.Implementation;

import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import com.divum.MeetingRoomBlocker.Repository.UserEntityRepository;
import com.divum.MeetingRoomBlocker.Service.UserServices;
import com.divum.MeetingRoomBlocker.Constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServicesImplementation implements UserServices {

    private final UserEntityRepository userEntityRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findByEmail(username);
        if(userEntityOptional.isEmpty()){
            throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
        }
        return userEntityOptional.get();
    }

}
