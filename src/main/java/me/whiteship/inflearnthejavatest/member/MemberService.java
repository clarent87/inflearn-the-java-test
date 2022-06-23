package me.whiteship.inflearnthejavatest.member;

import me.whiteship.inflearnthejavatest.domain.Member;

import java.util.Optional;

public interface MemberService {
    // 이건 사용하지 않음
    void validate(Long memberId) throws InvalidMemberException;

    Optional<Member> findById(Long memberId);
}
