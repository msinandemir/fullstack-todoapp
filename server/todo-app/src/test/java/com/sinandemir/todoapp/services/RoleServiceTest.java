package com.sinandemir.todoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.sinandemir.todoapp.entities.Role;
import com.sinandemir.todoapp.repositories.RoleRepository;

@SpringBootTest
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepos;

    @InjectMocks
    private RoleService cut;


    @Test
    @DisplayName("Test findByName")
    void should_find_by_name_with_role_name(){
        String roleName = "FAKE_ROLE";
        Role role = new Role();
        role.setName(roleName);

        when(roleRepos.findByName(roleName)).thenReturn(role);

        Role result = cut.findByName(roleName);

        assertNotNull(result);
        assertEquals(roleName, result.getName());

        verify(roleRepos).findByName(roleName);
    }
    
}
