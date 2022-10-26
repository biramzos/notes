package com.moodle.notes.Models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="groups")
@Setter
@Getter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String groupname;
    @Column
    private String tutor;
    @Column
    private String students;
    @Column
    private String teachers;

    public Group(){}

    public Group(String groupname, String tutor, String students, String teachers){
        this.groupname = groupname;
        this.tutor = tutor;
        this.students = students;
        this.teachers = teachers;
    }
}
