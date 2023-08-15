package com.sinandemir.todoapp.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinandemir.todoapp.dto.requests.TodoRequest;
import com.sinandemir.todoapp.dto.responses.TodoResponse;
import com.sinandemir.todoapp.services.TodoService;

@RestController
@RequestMapping("api/v1/todos")
public class TodoController {

    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoResponse> addTodo(@RequestBody TodoRequest todoRequest) {
        TodoResponse savedTodo = todoService.addTodo(todoRequest);
        return new ResponseEntity<TodoResponse>(savedTodo, HttpStatus.CREATED);
    }

    @GetMapping("{todoId}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long todoId) {
        TodoResponse todo = todoService.getTodo(todoId);
        return new ResponseEntity<TodoResponse>(todo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return new ResponseEntity<List<TodoResponse>>(todos, HttpStatus.OK);
    }

    @PutMapping("{todoId}")
    public ResponseEntity<TodoResponse> updateTodo(@RequestBody TodoRequest todoRequest, @PathVariable Long todoId) {
        TodoResponse todo = todoService.updateTodo(todoRequest, todoId);
        return new ResponseEntity<TodoResponse>(todo, HttpStatus.OK);
    }

    @DeleteMapping("{todoId}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long todoId){
        todoService.deleteTodo(todoId);
        return new ResponseEntity<String>("todo deleted successfully.",HttpStatus.OK);
    }

    @PatchMapping("{todoId}")
    public ResponseEntity<TodoResponse> changeCompletedStatus(@PathVariable Long todoId){
        TodoResponse todo = todoService.changeCompletedStatus(todoId);
        return new ResponseEntity<TodoResponse>(todo, HttpStatus.OK);
    }
}
