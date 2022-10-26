package com.moodle.notes.Services;

import com.moodle.notes.DataAccessObject.AdminRequest;
import com.moodle.notes.Generators.HashGenerator;
import com.moodle.notes.Models.Admin;
import com.moodle.notes.Repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository repository;

    @Nullable
    public HashMap<String, String> add(AdminRequest request) throws NoSuchAlgorithmException {
        Admin newAdmin = repository.findAdminByUsername(request.getUsername());
        if(newAdmin == null){
            repository.save(new Admin(request.getUsername(), HashGenerator.generate(request.getPassword())));
            return new HashMap<String, String>(){{
                put("username",request.getUsername());
                put("message","Success");
            }};
        }
        return null;
    }

    @Nullable
    public HashMap<String, String> login(AdminRequest request) throws NoSuchAlgorithmException {
        Admin admin = repository.findAdminByUsername(request.getUsername());
        if(admin != null){
            if(admin.getPassword().equals(HashGenerator.generate(request.getPassword()))){
                return new HashMap<String, String>(){{
                    put("username",request.getUsername());
                    put("message","Success");
                }};
            }
        }
        return null;
    }

    @Nullable
    public Admin getAdminByUsername(String username){
        return repository.findAdminByUsername(username);
    }

    @Nullable
    public List<Admin> getAdmins(){
        return repository.findAll();
    }

    @Nullable
    public List<Admin> findAdmins(String search){
        List<Admin> admins = new ArrayList<>();
        for(Admin admin: repository.findAll()){
            if(admin.getUsername().toLowerCase().contains(search.toLowerCase())){
                admins.add(admin);
            }
        }
        return admins;
    }

    @Nullable
    public void deleteByUsername(String username){
        repository.delete(repository.findAdminByUsername(username));
    }
}
