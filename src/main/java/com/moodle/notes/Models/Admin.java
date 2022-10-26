package com.moodle.notes.Models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="admins")
@Setter
@Getter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    public Admin(){}

    public Admin(String username, String password){
        this.username = username;
        this.password = password;
    }

}
