package com.moodle.notes.DataAccessObject;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteRequest {
    private String title;
    private String content;
}
