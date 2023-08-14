package com.sinandemir.todoapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.sinandemir.todoapp.dto.requests.TodoRequest;
import com.sinandemir.todoapp.dto.responses.TodoResponse;
import com.sinandemir.todoapp.entities.Todo;
import com.sinandemir.todoapp.exceptions.ResourceNotFoundException;
import com.sinandemir.todoapp.repositories.TodoRepository;

@Service
public class TodoService {

    private TodoRepository todoRepos;
    private ModelMapper modelMapper;

    public TodoService(TodoRepository todoRepos, ModelMapper modelMapper) {
        this.todoRepos = todoRepos;
        this.modelMapper = modelMapper;
    }

    public TodoResponse addTodo(TodoRequest todoRequest) {
        Todo todo = modelMapper.map(todoRequest, Todo.class);
        Todo savedTodo = todoRepos.save(todo);
        TodoResponse todoResponse = modelMapper.map(savedTodo, TodoResponse.class);
        return todoResponse;
    }

    public TodoResponse getTodo(Long todoId) {
        Todo todo = todoRepos.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("todo not found with id -> " + todoId));

        TodoResponse mappedTodo = modelMapper.map(todo, TodoResponse.class);
        return mappedTodo;
    }

    public List<TodoResponse> getAllTodos() {
        List<Todo> todos = todoRepos.findAll();
        List<TodoResponse> mappedTodos = todos.stream().map((todo) -> modelMapper.map(todo, TodoResponse.class))
                .collect(Collectors.toList());
        return mappedTodos;
    }
}
