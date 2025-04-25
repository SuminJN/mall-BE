package my.mallapi.service;

import my.mallapi.domain.Member;
import my.mallapi.dto.MemberDTO;
import my.mallapi.dto.MemberModifyDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    void modifyMember(MemberModifyDTO memberModifyDTO);

    default MemberDTO entityToDTO(Member member) {

        MemberDTO dto = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream().map(memberRole -> memberRole.name())
                        .collect(Collectors.toList()));

        return dto;
    }
}
