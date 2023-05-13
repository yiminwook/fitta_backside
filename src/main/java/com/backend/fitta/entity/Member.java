package com.backend.fitta.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Auditing {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String name;
    private int age;
    private String address;
    private Gender gender;
    private Integer height;
    private Integer weight;
    private String occupation;
    private String note;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_name")
    private Team team;

    public Member(String email, String name, int age, String address, Gender gender, Integer height, Integer weight, String occupation, String note, Team team) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.occupation = occupation;
        this.note = note;
        if(team!=null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team=team;
        team.getMembers().add(this);
    }

}
