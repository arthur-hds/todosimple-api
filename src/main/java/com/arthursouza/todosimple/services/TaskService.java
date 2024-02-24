package com.arthursouza.todosimple.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthursouza.todosimple.models.Task;
import com.arthursouza.todosimple.models.User;
import com.arthursouza.todosimple.models.enums.ProfileEnum;
import com.arthursouza.todosimple.repositories.TaskRepository;
import com.arthursouza.todosimple.security.UserSpringSecurity;
import com.arthursouza.todosimple.services.exceptions.DataBindingViolationException;
import com.arthursouza.todosimple.services.exceptions.ObjectNotFoundException;


@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    /* A "findById" method that calls JPARepository, but designed to be able to show an error message when not found */
    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Task não encontrada! Id: "+id+ ", Tipo: "+ Task.class.getName()
            ));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) 
            && !userHasTasks(userSpringSecurity, task)) {
            throw new ObjectNotFoundException("Access denied");
        }
        
        return task;
        
    }


    public List<Task> findAllByUser(){

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)) {
            throw new ObjectNotFoundException("Access denied");
        }

        List<Task> tasks = this.taskRepository.findByUser_id(userSpringSecurity.getId());
        return tasks;

    }


    public List<Task> findAllTasks(){

        
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN)) {
            throw new ObjectNotFoundException("Access denied");
        }

        List<Task> tasks = this.taskRepository.findTasks();
        return tasks;


    }





    /*CRUD Methods */
    @Transactional
    public Task create(Task obj){

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)) {
            throw new ObjectNotFoundException("Access denied");
        }
        
        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }
    
    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    @Transactional
    public void delete(Long id){
        Task task = findById(id);
        try {
            this.taskRepository.delete(task);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir a Task, pois há entidades relacionadas");
        }
    }


    private Boolean userHasTasks(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());

    }

}
