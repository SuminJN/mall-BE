package my.mallapi.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import my.mallapi.member.domain.Member;
import my.mallapi.member.domain.MemberRole;
import my.mallapi.member.dto.MemberDTO;
import my.mallapi.member.dto.MemberModifyDTO;
import my.mallapi.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {

        String email = getEmailFromKakaoAccessToken(accessToken);

        log.info("email: {}", email);

        Optional<Member> result = memberRepository.findByEmail(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니면
        // 닉네임은 "소셜회원"으로
        // 패스워드는 임의로 생성
        Member socialMember = makeSocialMember(email);
        memberRepository.save(socialMember);

        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    private String getEmailFromKakaoAccessToken(String accessToken) {

        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("------------------------------------");
        log.info(bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");

        log.info("kakaoAccount: {}", kakaoAccount);

        return kakaoAccount.get("email");
    }

    private String makeTempPassword() {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }

        return buffer.toString();
    }

    private Member makeSocialMember(String email) {

        String tempPassword = makeTempPassword();

        log.info("tempPassword: {}", tempPassword);

        String nickname = "소셜회원";

        Member member = Member.builder()
                .email(email).pw(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .social(true)
                .build();

        member.addRole(MemberRole.USER);

        return member;
    }

    @Override
    public MemberDTO getGoogleMember(String code) {

        String email = getEmailFromGoogleAccessToken(code);

        log.info("email: {}", email);

        Optional<Member> result = memberRepository.findByEmail(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니면
        // 닉네임은 "소셜회원"으로
        // 패스워드는 임의로 생성
        Member socialMember = makeSocialMember(email);
        memberRepository.save(socialMember);

        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    private String getEmailFromGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 토큰 요청
        String accessToken = requestGoogleAccessToken(restTemplate, code);

        log.info("구글 accessToken: {}", accessToken);

        // 사용자 정보 요청
        return requestGoogleUserEmail(restTemplate, accessToken);
    }

    private String requestGoogleAccessToken(RestTemplate restTemplate, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", "http://localhost:3000/member/google");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity("https://oauth2.googleapis.com/token", tokenRequest, Map.class);
        return (String) tokenResponse.getBody().get("access_token");
    }

    private String requestGoogleUserEmail(RestTemplate restTemplate, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> userRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> userResponse = restTemplate.exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, userRequest, Map.class);
        Map userInfo = userResponse.getBody();
        return (String) userInfo.get("email");
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {

        Optional<Member> result = memberRepository.findByEmail(memberModifyDTO.getEmail());

        Member member = result.orElseThrow();

        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));
        member.changeSocial(false);
        member.changeNickname(memberModifyDTO.getNickname());

        memberRepository.save(member);
    }
}
