package com.sinandemir.todoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.sinandemir.todoapp.dto.requests.TodoRequest;
import com.sinandemir.todoapp.dto.responses.TodoResponse;
import com.sinandemir.todoapp.entities.Todo;
import com.sinandemir.todoapp.exceptions.ResourceNotFoundException;
import com.sinandemir.todoapp.repositories.TodoRepository;

@SpringBootTest
public class TodoServiceTest {
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TodoService cut;

    @Mock
    private TodoRepository todoRepos;

    @Captor
    private ArgumentCaptor<Todo> todoCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getTodo called repository")
    void should_get_todo_by_id() {
        Long todoId = 0L;

        Todo result = new Todo();
        result.setTitle("someValue");
        result.setDescription("someValue");
        result.setCompleted(false);

        Mockito.when(todoRepos.findById(todoId)).thenReturn(Optional.of(result));

        cut.getTodo(todoId);

        verify(todoRepos).findById(todoId);
    }

    @Test
    @DisplayName("Test getTodo throws an exception")
    void should_throw_exception_get_demo() {
        Long todoId = 0L;

        Mockito.when(todoRepos.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.getTodo(todoId);
        });

        assertEquals("todo not found with id -> " + todoId, ex.getMessage());
    }

    @Test
    @DisplayName("Test getAllTodos called repository")
    void should_get_all_todos() {
        List<Todo> todos = new ArrayList<Todo>();
        Todo todo1 = new Todo(0L, "someValue", "someValue", false);
        Todo todo2 = new Todo(1L, "someValue", "someValue", false);
        todos.add(todo1);
        todos.add(todo2);

        Mockito.when(todoRepos.findAll()).thenReturn(todos);

        List<TodoResponse> result = cut.getAllTodos();

        assertEquals(todos.size(), result.size());
    }

    @Test
    @DisplayName("Test add todo")
    void should_add_todo() {

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("someValue");
        todoRequest.setDescription("someValue");

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("someValue");
        todo.setDescription("someValue");

        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("someValue");
        todoResponse.setDescription("someValue");

        when(modelMapper.map(todoRequest, Todo.class)).thenReturn(todo);
        when(todoRepos.save(todo)).thenReturn(todo);
        when(modelMapper.map(todo, TodoResponse.class)).thenReturn(todoResponse);

        TodoResponse result = cut.addTodo(todoRequest);

        assertNotNull(result);
        assertEquals(todoResponse, result);

        verify(modelMapper).map(todoRequest, Todo.class);
        verify(todoRepos).save(todo);
        verify(modelMapper).map(todo, TodoResponse.class);

        assertNotNull(result);
        assertNotNull(result.getDescription());
        assertNotNull(result.getTitle());
        assertNotNull(result.isCompleted());
        assertEquals(todoRequest.getTitle(), result.getTitle());
        assertEquals(todoRequest.getDescription(), result.getDescription());
        assertEquals(todoRequest.isCompleted(), result.isCompleted());

    }

    @Test
    @DisplayName("Test deleteTodo")
    void should_delete_todo() {
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setTitle("someValue");
        todo.setDescription("someValue");
        todo.setCompleted(false);

        when(todoRepos.findById(todoId)).thenReturn(Optional.of(todo));
        doNothing().when(todoRepos).deleteById(todoId);

        cut.deleteTodo(todoId);

        verify(todoRepos).findById(todoId);
        verify(todoRepos).deleteById(todoId);
    }

    @Test
    @DisplayName("Test deleteTodo throws an exception")
    void should_throw_exception_delete_todo() {
        Long todoId = 1L;

        when(todoRepos.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.getTodo(todoId);
        });

        assertEquals("todo not found with id -> " + todoId, ex.getMessage());

    }

    @Test
    @DisplayName("Test updateTodo")
    void should_update_todo() {
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setTitle("someValue");
        todo.setDescription("someValue");
        todo.setCompleted(false);

        when(todoRepos.findById(todoId)).thenReturn(Optional.of(todo));

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("someChengedValue");
        todoRequest.setDescription("someChengedValue");
        todoRequest.setCompleted(true);

        cut.updateTodo(todoRequest, todoId);

        verify(todoRepos).save(todoCaptor.capture());

        Todo capturedTodo = todoCaptor.getValue();

        assertNotNull(capturedTodo);
        assertNotNull(capturedTodo.getId());
        assertNotNull(capturedTodo.getTitle());
        assertNotNull(capturedTodo.getDescription());
        assertNotNull(capturedTodo.isCompleted());

        assertEquals(capturedTodo.getId(), todoId);
        assertEquals(capturedTodo.getTitle(), todoRequest.getTitle());
        assertEquals(capturedTodo.getDescription(), todoRequest.getDescription());
        assertEquals(capturedTodo.isCompleted(), todoRequest.isCompleted());
    }

    @Test
    @DisplayName("Test updateTodo throws an exception")
    void should_throw_exception_update_todo() {
        Long todoId = 1L;

        when(todoRepos.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.getTodo(todoId);
        });

        assertEquals("todo not found with id -> " + todoId, ex.getMessage());

    }

    @Test
    @DisplayName("Test changeCompletedStatus")
    void should_change_completed_status() {
        Long todoId = 1L;

        Todo todo = new Todo();
        when(todoRepos.findById(todoId)).thenReturn(Optional.of(todo));

        when(todoRepos.save(any(Todo.class))).thenReturn(todo);

        when(modelMapper.map(todo, TodoResponse.class)).thenReturn(new TodoResponse());

        TodoResponse result = cut.changeCompletedStatus(todoId);

        verify(todoRepos).findById(todoId);
        verify(todoRepos).save(todo);
        verify(modelMapper).map(todo, TodoResponse.class);

        assertNotNull(result);
        assertNotEquals(todo.isCompleted(), result.isCompleted());
    }

    @Test
    @DisplayName("Test changeCompletedStatus throws an exception")
    void should_throw_exception_change_completed_status() {
        Long todoId = 1L;

        when(todoRepos.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.getTodo(todoId);
        });

        assertEquals("todo not found with id -> " + todoId, ex.getMessage());
    }
}
