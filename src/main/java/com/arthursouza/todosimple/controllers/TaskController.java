package com.arthursouza.todosimple.controllers;

import java.net.URI;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

import com.arthursouza.todosimple.models.Task;
import com.arthursouza.todosimple.models.projection.TaskProjection;
import com.arthursouza.todosimple.services.TaskService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/task")
public class TaskController {
    
    @Autowired
    private TaskService taskService;




    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id){
        Task task = this.taskService.findById(id);
        return ResponseEntity.ok().body(task);
    }


    @GetMapping("/user")
    public ResponseEntity<List<TaskProjection>> findAllByUser(){
        List<TaskProjection> tasks = this.taskService.findAllByUser();
        return ResponseEntity.ok().body(tasks);

    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskProjection>> findAllTasks(){
        List<TaskProjection> tasks = this.taskService.findAllTasks();
        return ResponseEntity.ok().body(tasks);
        
    }

    /* CRUD Methods */

    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj){
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(obj.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Valid @RequestBody Task obj, @PathVariable Long id){
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();

    }



}
