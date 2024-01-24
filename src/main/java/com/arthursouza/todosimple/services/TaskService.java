package com.arthursouza.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthursouza.todosimple.models.Task;
import com.arthursouza.todosimple.models.User;
import com.arthursouza.todosimple.repositories.TaskRepository;


@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    /* A "findById" method that calls JPARepository, but designed to be able to show an error message when not found */
    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
            "Task não encontrada! Id: "+id+ ", Tipo: "+ Task.class.getName()
            ));
    }


    public List<Task> findAllUsersById(Long id){
        this.userService.findById(id);
        List<Task> tasks = this.taskRepository.findByUser_id(id);
        return tasks;


    }

    /*CRUD Methods */
    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
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
            throw new RuntimeException("Não é possível excluir a Task, pois há entidades relacionadas");
        }
    }

}
