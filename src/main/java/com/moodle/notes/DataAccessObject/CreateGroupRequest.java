package com.moodle.notes.DataAccessObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    private String groupname;
    private String tutor;
    private String students;
    private String teachers;
}
