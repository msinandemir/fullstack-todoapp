package com.sinandemir.todoapp.services;

import org.springframework.stereotype.Service;

import com.sinandemir.todoapp.entities.Role;
import com.sinandemir.todoapp.repositories.RoleRepository;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
