package com.arthursouza.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arthursouza.todosimple.models.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    
    //User findById();

}
