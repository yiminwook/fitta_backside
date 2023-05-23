package com.backend.fitta.dto.team;

import com.backend.fitta.entity.enums.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MemberTeamResponse {
    private String email;
    private String name;
    private Gender gender;
    private String address;
    private String phoneNumber;
    private LocalDate birthday;

    @QueryProjection
    public MemberTeamResponse(String email, String name, LocalDate birthday, String phoneNumber, String address, Gender gender) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
    }
}
