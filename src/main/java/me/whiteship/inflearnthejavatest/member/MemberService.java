package me.whiteship.inflearnthejavatest.member;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validate(Long memberId);

    // 해당 스터디에 관심있는 사람들에게 알림줌
    void notify(Study newstudy);

    void notify(Member member);
}
