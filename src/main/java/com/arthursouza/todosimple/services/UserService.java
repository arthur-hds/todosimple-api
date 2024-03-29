package com.arthursouza.todosimple.services;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthursouza.todosimple.models.User;
import com.arthursouza.todosimple.models.dto.UserCreateDTO;
import com.arthursouza.todosimple.models.dto.UserUpdateDTO;
import com.arthursouza.todosimple.models.enums.ProfileEnum;
import com.arthursouza.todosimple.repositories.UserRepository;
import com.arthursouza.todosimple.security.UserSpringSecurity;
import com.arthursouza.todosimple.services.exceptions.AuthorizationException;
import com.arthursouza.todosimple.services.exceptions.DataBindingViolationException;
import com.arthursouza.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.validation.Valid;



@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private UserRepository userRepository;




    /* A "findById" method that calls JPARepository, but designed to be able to show an error message when not found */
    public User findById(long id){
        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !(userSpringSecurity.getId()).equals(id) ) {
            throw new AuthorizationException("User denied");
        }


        Optional<User> user =  this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! Id: "+id+ ", Tipo: "+ User.class.getName()
        ));
    }



    
    /* CRUD methods */
    @Transactional
    public User create (User obj){
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));;
        return this.userRepository.save(newObj);
    }

    @Transactional
    public void delete(Long id){
        User obj = findById(id);

        try {
            this.userRepository.delete(obj);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir o usuário, pois há entidades relacionadas");
        }
        

    }


    public static UserSpringSecurity authenticated(){
        try {

            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        } catch (Exception e) {
            return null;
        }


    }


    public User fromDTO(@Valid UserCreateDTO obj) {
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }


    public User fromDTO(@Valid UserUpdateDTO obj) {
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }


}
