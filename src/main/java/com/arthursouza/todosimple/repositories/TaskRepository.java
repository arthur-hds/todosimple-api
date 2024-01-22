package com.arthursouza.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.arthursouza.todosimple.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
    
    //List<Task> findByUser_id(Long id);

    //Optional<Task> findById(Long id);

    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    //List<Task> findByUser_id(@Param("id") Long id);

    //@Query(value = "SELECT * FROM task t WHERE t.user_id = :id" ,nativeQuery = true)
    //List<Task> findByUser_id(@Param("id") Long id);
}
