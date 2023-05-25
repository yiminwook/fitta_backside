package com.backend.fitta.repository;

import com.backend.fitta.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, StaffRepositoryCustom {
    Optional<Member> findByEmail(String email);
}
