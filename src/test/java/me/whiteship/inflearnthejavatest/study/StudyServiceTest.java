package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.member.InvalidMemberException;
import me.whiteship.inflearnthejavatest.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class) // @Mock 어노테이션 처리를 위해 필요.  FindSlowTestExtension 처럼 Extension 만든것
class StudyServiceTest {

    /**
     * 기본적인 mockito mock 원리
     */
    @Test
    void createStudyTest0() {

        // 이렇게 interface를 만들어서 넣어주면 되는데.. 너무 번거로움
        MemberService memberService = new MemberService() {
            @Override
            public void validate(Long memberId) throws InvalidMemberException {

            }

            @Override
            public Optional<Member> findById(Long memberId) {
                return Optional.empty();
            }
        };

        // 위와 같은 작업을 Mockito가 대신 해줌
        // param으로 class나 interface의 type을 전달
        // ( .class까지 붙이면 type이라고하나? 보통 class를 그냥 type이라고 하던데.. 어디서 그랬지? )
        StudyRepository studyRepository = mock(StudyRepository.class);

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService); // null이면 error
    }

    /**
     * mock annotation으로 코드를 줄임
     * @ExtendWith(MockitoExtension.class) 이거 반드시 필요. @Mock 어노테이션 처리 해주는 Extension이 있어야해서
     * 이렇게 test field로 만들어 두는 경우는 다른 Test method에서도 mock을 이용하는 경우.
     * > 근데 짜피 Test class instance는 method마다 만들어 지므로.. 원래 class field를 만들어서 method에 사용하는
     * > 그 의미와는 다름..
     */
    @Mock
    MemberService memberService;
    @Mock
    StudyRepository studyRepository;

    @Test
    void createStudyTest1() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService); // null이면 error

    }

    /**
     * class field에 Mock을 만들지 않고 아래 처럼 method param으로 받을수도 있음
     */
    @Test
    void createStudyService(@Mock MemberService memberService,
                          @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService); // null이면 error
    }

}