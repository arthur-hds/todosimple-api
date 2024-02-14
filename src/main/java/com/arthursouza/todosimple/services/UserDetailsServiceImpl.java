package com.arthursouza.todosimple.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.arthursouza.todosimple.models.User;
import com.arthursouza.todosimple.repositories.UserRepository;
import com.arthursouza.todosimple.security.UserSpringSecurity;

@Service
public class UserDetailsServiceImpl {
    
    @Autowired
    private UserRepository userRepository;

    //Give authorities to the following User
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        User user = userRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not founded: "+ username);
        }


        return new UserSpringSecurity(user.getId(), user.getUsername(), user.getPassword(), user.getProfiles());

    }


}
