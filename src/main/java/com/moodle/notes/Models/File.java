package com.moodle.notes.Models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="files")
@Setter
@Getter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String name;
    @Column
    private byte[] content;
    @Column
    private Long noteId;

    public File(){}

    public File(String name, byte[] content, Long noteId){
        this.name = name;
        this.content = content;
        this.noteId = noteId;
    }
}
