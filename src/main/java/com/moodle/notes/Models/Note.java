package com.moodle.notes.Models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "notes")
@Getter
@Setter
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private String groupname;

    public Note(){}

    public Note(String title,String content, String groupname){
        this.title = title;
        this.content = content;
        this.groupname = groupname;
    }
}
