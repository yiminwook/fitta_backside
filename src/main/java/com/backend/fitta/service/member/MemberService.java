package com.backend.fitta.service.member;

import com.backend.fitta.config.jwt.JwtTokenProvider;
import com.backend.fitta.config.jwt.TokenInfo;
import com.backend.fitta.dto.Result;
import com.backend.fitta.dto.member.BasicMemberInfo;
import com.backend.fitta.dto.member.SignUpRequest;
import com.backend.fitta.dto.member.UpdateMemberRequest;
import com.backend.fitta.entity.enums.Role;
import com.backend.fitta.entity.gym.Gym;
import com.backend.fitta.entity.gym.Team;
import com.backend.fitta.entity.member.Member;
import com.backend.fitta.exception.*;
import com.backend.fitta.repository.gym.GymRepository;
import com.backend.fitta.repository.member.MemberRepository;
import com.backend.fitta.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final GymRepository gymRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInfo login(String email,String password){
        //1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이 때, authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken  = new UsernamePasswordAuthenticationToken(email,password);
        log.info("authenticationToken={}",authenticationToken);
        /*
        ex : authenticationToken=
        UsernamePasswordAuthenticationToken
        [
        Principal=email@email.com,
        Credentials=[PROTECTED],
        Authenticated=false,
        Details=null,
        Granted Authorities=[]
        ]
        */

        //2. 실제 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication={}",authentication);
        /*ex : authentication=
        UsernamePasswordAuthenticationToken [
        Principal=
        org.springframework.security.core.userdetails.User
        [
        Username=
        email@email.com,
        Password=[PROTECTED],
        Enabled=true,
        AccountNonExpired=true,
        credentialsNonExpired=true,
        AccountNonLocked=true,
        Granted Authorities=[ROLE_USER]
        ],
        Credentials=[PROTECTED],
        Authenticated=true,
        Details=null,
        Granted Authorities=[ROLE_USER]
        ]
        */

        //3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        log.info("tokenInfo={}",tokenInfo);
        return tokenInfo;
        /*
        ex : tokenInfo=TokenInfo(
        grantType=Bearer,
        accessToken=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjg1NjkwMTk0fQ._rU-PWMo2HF-aisInq95C7DIwEsvtyrWq3olckGW7aY,
        refreshToken=eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODU2OTAxOTR9.CevfTfOuDWooA_LrllBhk8vQPxa-lP2QHANLEMP9FMY
        )
        */
    }

    public Long save(SignUpRequest rq) {
        Optional<Member> findMember = memberRepository.findByEmail(rq.getEmail());
        if (!findMember.isEmpty()) { //중복 체크
            throw new AlreadyExistMemberException();
        }
        if (!rq.getPassword().equals(rq.getPasswordConfirm())) {
            throw new PWNotCorrespondException();
        }
        Member member = new Member(rq.getEmail(), rq.getPassword(), rq.getName(), rq.getBirthday(), rq.getPhoneNumber(), rq.getAddress()
                , rq.getGender(), null, null, rq.getOccupation(), null, null, null, Role.MEMBER);
        memberRepository.save(member);
        return member.getId();
    }


    public BasicMemberInfo findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return new BasicMemberInfo(member);
    }

    public Result<List<BasicMemberInfo>> findAll() {
        List<Member> all = memberRepository.findAll();
        List<BasicMemberInfo> collect = all.stream()
                .map(m -> new BasicMemberInfo(m))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    public Long update(Long memberId, UpdateMemberRequest rq) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.changeMemberInfo(rq.getEmail(), rq.getPassword(),rq.getName(), rq.getBirthday(), rq.getPhoneNumber(), rq.getAddress(), rq.getHeight(), rq.getWeight(), rq.getOccupation(), rq.getNote());
        return member.getId();
    }


    public void saveTeamMember(long memberId, long teamId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException());
        findMember.changeTeam(team);
    }


    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }


    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public void saveGymMember(long memberId, long gymId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
        Gym gym = gymRepository.findById(gymId).orElseThrow(() -> new GymNotFoundException());
        findMember.changeGym(gym);
    }

    /**
     * 임시 추가분, 추후 정리 필요
     */
    /*로그인 후 */
    public BasicMemberInfo findByEmail(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException());
        BasicMemberInfo basicMemberInfo = new BasicMemberInfo(member);
        return basicMemberInfo;
    }
}
