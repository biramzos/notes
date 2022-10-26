package com.moodle.notes.Repositories;

import com.moodle.notes.Models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupByGroupname(String groupname);
    List<Group> findAll();
}
