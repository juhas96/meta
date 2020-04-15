package mp.example;

//
//import sk.tuke.mp.annotations.Column;
//import sk.tuke.mp.annotations.Entity;
//import sk.tuke.mp.annotations.Id;

import javax.persistence.*;

@Entity
@Table(name = "OSOBA")
public class Person {
    @Id
    private int id;

    @Column(name = "PRIEZVISKO", nullable = false)
    private String surname;

    @Column(name = "MENO", length = 100)
    private String name;

    @Transient
    private int age;

    @ManyToOne(targetEntity = Department.class, fetch = FetchType.LAZY)
    private Department department;

    public Person(String surname, String name, int age) {
        this.surname = surname;
        this.name = name;
        this.age = age;
    }

    public Person() {
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }


    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return String.format("Person %d: %s %s (%d)", id, surname, name, age);
    }
}

