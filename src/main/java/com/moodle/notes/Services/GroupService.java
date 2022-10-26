package com.moodle.notes.Services;

import com.moodle.notes.DataAccessObject.CreateGroupRequest;
import com.moodle.notes.DataAccessObject.GroupRequest;
import com.moodle.notes.Generators.HashGenerator;
import com.moodle.notes.Models.Group;
import com.moodle.notes.Models.Role;
import com.moodle.notes.Repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service

public class GroupService{
    @Autowired
    private GroupRepository repository;

    public HashMap<String,String> login(GroupRequest request) throws NoSuchAlgorithmException {
        Group group = repository.findGroupByGroupname(request.getGroupname());
        if(group != null){
            if(request.getRole().equals("STUDENTS")){
                if(group.getStudents().equals(HashGenerator.generate(request.getPassword()))){
                    return new HashMap<>(){{
                        put("role",Role.STUDENTS.name());
                        put("groupname", group.getGroupname());
                    }};
                }
                else{
                    return new HashMap<>(){{
                        put("role","");
                        put("groupname", "");
                    }};
                }
            }
            else if(request.getRole().equals("TEACHERS")){
                if(group.getTeachers().equals(HashGenerator.generate(request.getPassword()))){
                    return new HashMap<>(){{
                        put("role",Role.TEACHERS.name());
                        put("groupname", group.getGroupname());
                    }};
                }
                else{
                    return new HashMap<>(){{
                        put("role", "");
                        put("groupname", "");
                    }};
                }
            }
            else{
                return new HashMap<>(){{
                    put("role", "");
                    put("groupname", "");
                }};
            }
        }
        return null;
    }

    @Nullable
    public List<Group> getGroups(){
        return repository.findAll();
    }

    public List<String> getGroupnames(){
        List<String> groupnames = new ArrayList<>();
        for(Group group: repository.findAll()){
            groupnames.add(group.getGroupname());
        }
        return groupnames;
    }

    @Nullable
    public HashMap<String, String> register(CreateGroupRequest request) throws NoSuchAlgorithmException {
        Group group = repository.findGroupByGroupname(request.getGroupname());
        if(group == null){
            repository.save(new Group(request.getGroupname(), request.getTutor(), HashGenerator.generate(request.getStudents()), HashGenerator.generate(request.getTeachers())));
            return new HashMap<String, String>(){{
                put("groupname",request.getGroupname());
                put("message","Success");
            }};
        }
        return null;
    }

    public List<Group> findGroups(String search){
        List<Group> groups = new ArrayList<>();
        for(Group group: repository.findAll()){
            if(group.getGroupname().toLowerCase().contains(search.toLowerCase())){
                groups.add(group);
            }
            if(group.getTutor().toLowerCase().contains(search.toLowerCase())){
                groups.add(group);
            }
        }
        return groups;
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public Group getGroupByGroupname(String groupname){
        return repository.findGroupByGroupname(groupname);
    }
}
