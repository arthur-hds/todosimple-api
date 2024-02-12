package com.arthursouza.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthursouza.todosimple.models.User;
import com.arthursouza.todosimple.repositories.UserRepository;
import com.arthursouza.todosimple.services.exceptions.DataBindingViolationException;
import com.arthursouza.todosimple.services.exceptions.ObjectNotFoundException;



@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;




    /* A "findById" method that calls JPARepository, but designed to be able to show an error message when not found */
    public User findById(long id){
        Optional<User> user =  this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! Id: "+id+ ", Tipo: "+ User.class.getName()
        ));
    }

    /* CRUD methods */
    @Transactional
    public User create (User obj){
        obj.setId(null);
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
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



}
