package com.moodle.notes.DataAccessObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRequest {
    private String groupname;
    private String password;
    private String role;
}
