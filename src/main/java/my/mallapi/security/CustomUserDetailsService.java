package my.mallapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import my.mallapi.domain.Member;
import my.mallapi.dto.MemberDTO;
import my.mallapi.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 사용자 정보 조회 및 인증과 권한 처리
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("-----------------loadUserByUsername------------------");

        Member member = memberRepository.getWithRoles(username);

        if (member == null) {
            throw new UsernameNotFoundException("Not Found Member");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).toList()
        );

        log.info(memberDTO);

        return memberDTO;
    }
}
