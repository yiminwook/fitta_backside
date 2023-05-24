package com.backend.fitta.controller.google;


import com.backend.fitta.dto.google.AccountInfo;
import com.backend.fitta.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

@RestController
@RequestMapping(produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증",description ="OAuth2.0을 이용한 google 로그인 로직입니다" )
public class LoginController {
    private final LoginService loginService;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;
    private String loginPage;

    /**
     * 구글 로그인 페이지로 이동합니다
     * @param
     * @throws IOException
     */
     @Operation(summary = "로그인 url 리다이렉션 메서드",description = "현재 프론트와 조정중 입니다.")
     @GetMapping("/auth/sign")
    public ResponseEntity<Urls> googleLogin() throws IOException {
        String scope = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
        String frontRedirectUrl ="https://fitta-git-dev-yiminwook.vercel.app/";
        String encodedScope = URLEncoder.encode(scope, "UTF-8");
        String encodeRedirectUrl = URLEncoder.encode(redirectUri, "UTF-8");
        loginPage="https://accounts.google.com/o/oauth2/auth/oauthchooseaccount?client_id="+clientId+"&redirect_uri="+encodeRedirectUrl+"&response_type=code&scope="+encodedScope;

        Urls urls = new Urls(loginPage);
         ResponseEntity.ok().body(urls);
        return ResponseEntity.ok(urls);
    }

    /**
     * 구글에서 보내주는 값을 받아 AccountInfo 객체로 변환합니다.
     * @param code
     * @param registrationId
     * @return
     */
    @Operation(summary = "구글에서 리데이렉션 해서 보내주는 값을 받는 메서드",description = "사용자의 이메일, 이름, 사진을 받아옵니다")
    @GetMapping("/login/oauth2/code/{registrationId}")
    public ResponseEntity<AccountInfo> login(@RequestParam String code, @PathVariable String registrationId,HttpServletResponse response) throws JsonProcessingException {
        AccountInfo accountInfo = loginService.socialLogin(code, registrationId);
        log.info("userInfo={}",accountInfo);
        String redirectionUrl="https://fitta-git-dev-yiminwook.vercel.app/signup";
        ObjectMapper mapper = new ObjectMapper();
        String accountInfoJson = mapper.writeValueAsString(accountInfo);

        //URL로 인코딩
        String encode = URLEncoder.encode(accountInfoJson);

        //쿠키설정
        ResponseCookie cookie = ResponseCookie.from("accountInfo", encode)
                .httpOnly(true)
                .domain("fitta-git-dev-yiminwook.vercel.app")
                .path("/")
                .sameSite("none")
                .secure(true)
                .build();
        response.addHeader("Set-Cookie",cookie.toString());


        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create(redirectionUrl)).build();
    }
}
