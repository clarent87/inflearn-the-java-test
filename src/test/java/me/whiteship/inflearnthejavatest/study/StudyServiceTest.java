package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // @Mock 어노테이션 처리를 위해 필요.  FindSlowTestExtension 처럼 Extension 만든것
class StudyServiceTest {

    /**
     * mock annotation으로 코드를 줄임
     *
     * @ExtendWith(MockitoExtension.class) 이거 반드시 필요. @Mock 어노테이션 처리 해주는 Extension이 있어야해서
     * 이렇게 test field로 만들어 두는 경우는 다른 Test method에서도 mock을 이용하는 경우.
     * > 근데 짜피 Test class instance는 method마다 만들어 지므로.. 원래 class field를 만들어서 method에 사용하는
     * > 그 의미와는 다름..
     */
    @Mock
    MemberService memberService;
    @Mock
    StudyRepository studyRepository;

    /**
     * 기본적인 mockito mock 원리
     */
    @Test
    void createStudyTest0() {

        // 이렇게 interface를 만들어서 넣어주면 되는데.. 너무 번거로움
        MemberService memberService = new MemberService() {

            @Override
            public Optional<Member> findById(Long memberId) {
                return Optional.empty();
            }

            @Override
            public void validate(Long memberId) {

            }
        };

        // 위와 같은 작업을 Mockito가 대신 해줌
        // param으로 class나 interface의 type을 전달
        // ( .class까지 붙이면 type이라고하나? 보통 class를 그냥 type이라고 하던데.. 어디서 그랬지? )
        StudyRepository studyRepository = mock(StudyRepository.class);

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService); // null이면 error
    }

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

    /**
     * stubbing 예제
     * @param memberService
     * @param studyRepository
     */
    @Test
    void createNewStudy(@Mock MemberService memberService,
                        @Mock StudyRepository studyRepository) {

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        // 1. 모키토 기본 스터빙은 아래 처럼 진행
        when(memberService.findById(1L)) // memberService.findById(any()) 가 호출이 되면.
                .thenReturn(Optional.of(member));  // Optional.of(member)를 리턴 해라
        Optional<Member> memberOptional = memberService.findById(1L);
        assertEquals("keesun@email.com", memberOptional.get().getEmail());

        // 2. void method 호출시 예외 던지는 스터빙
        // 모키토 문서 Stubbing void methods with exceptions 에서 보면 void method는 특수하게도 예외 스터빙은 아래처럼 해야함
        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
        assertThrows(RuntimeException.class, () -> {
            memberService.validate(1L);
        });



        // 3. 메소드가 동일한 매개변수로 여러번 호출될 때 각기 다르게 행동호도록 조작할 수도 있다.
        // Stubbing consecutive calls
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member)) //  findById 첫번째 호출시
                .thenThrow(new RuntimeException()) //  findById 두번째 호출시
                .thenReturn(Optional.empty()); // findById 세번쨰 호출시


        Optional<Member> byId = memberService.findById(1L); // findById 첫번째 호출시
        assertEquals("keesun@email.com", byId.get().getEmail());

        assertThrows(RuntimeException.class, () -> {
            memberService.findById(2L); //  findById 두번째 호출시
        });

        assertEquals(Optional.empty(), memberService.findById(3L)); // findById 세번쨰 호출시
    }


}