package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Admin;

public interface AdminService {
    Admin findById(Long id);
    List<Admin> findAll();
    Admin save(Admin admin);
    Admin update(Long id, Admin adminDetails);
    void delete(Long id);
    Admin login(String correo, String contrasenia);
}