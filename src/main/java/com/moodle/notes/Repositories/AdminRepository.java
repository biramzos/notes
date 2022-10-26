package com.moodle.notes.Repositories;

import com.moodle.notes.Models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findAdminByUsername(String username);
    List<Admin> findAll();
}
