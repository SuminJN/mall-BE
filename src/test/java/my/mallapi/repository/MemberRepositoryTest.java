package my.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import my.mallapi.domain.Member;
import my.mallapi.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsertMember() {

        for (int i = 0; i < 10; i++) {

            Member member = Member.builder()
                    .email("user" + i + "@aaa.com")
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("USER" + i)
                    .build();

            member.addRole(MemberRole.USER);

            if(i >= 5) {
                member.addRole(MemberRole.MANAGER);
            }

            memberRepository.save(member);
        }
    }

    @Test
    public void testRead() {
        String email = "user9@aaa.com";

        Member member = memberRepository.getWithRoles(email);

        log.info("-------------------------------");
        log.info(member);
    }
}