package de.szut.lf8_project.model;

import  javax.persistence.*;
@Entity
@Table(name = "Projekt")
public class Projekt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String vorname;
    private String nachname;
}
