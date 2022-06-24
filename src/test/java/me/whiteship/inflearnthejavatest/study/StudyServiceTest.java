package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.domain.Study;
import me.whiteship.inflearnthejavatest.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

            @Override
            public void notify(Study newstudy) {

            }

            @Override
            public void notify(Member member) {

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
     *
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

    @Test
    void createNewStudyQuiz() {

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        Study study = new Study(10, "테스트");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        studyService.createNewStudy(1L, study);
        assertEquals(member, study.getOwner());

        //  studyService.createNewStudy 호출시 내부적을
        //  memberService.notify(newstudy); 가 호출되는데, 이게 호출이 잘 됬는지 알길이 없음
        //  memberService 는 mocking했고..
        //  이럴때 아래와 같은 verify를 써서 mock의 동작을 확인 가능 ( matcher사용 가능 )
        // 특정 시간안에 호출이 되야하는 게 있는경우 mockito의 timeout을 쓸수 있는데, 이럴 바에는 junit timeout을 쓰는게 낫다
        verify(memberService, times(1)).notify(study); // notify가 1번 study 매개변수로 호출됬어야 한다.
        verify(memberService, times(1)).notify(member);
        verify(memberService, never()).validate(any()); // validate 는 한번도 호출되지 않았어야한다.

        // 만약 notify 함수가 study로 한번 member로 한번 순서대로 호출되었는지 검증하려면?
        // 이건 쫌 너무한 test 같다고는함. ( 순서까지 확인하는거.. )
        // > 위쪽에서 verify한건 호출된 횟수 파악.하는거고 순서 확인에 영향을 주지는 않네.
        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study); // study로 먼저 호출되고
        inOrder.verify(memberService).notify(member);// member로 호출되어야한다.

        verifyNoInteractions(memberService); // 이거 호출된 이후로 더이상 memberService mock에 상호작용이 있어서는 안된다.

    }

    // BDD를 따르려면 test 이름도 should~~ 로 되어야함. (https://matheus.ro/2017/09/24/unit-test-naming-convention/)
    // 근데 그냥 display name만 잘 써줘도 될거 같다함.
    @Test
    void createNewStudyBDD() {
        //Givne
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        Study study = new Study(10, "테스트");

        // stubbing하는 부분은 BDD에 따르면 given에 해당하는데,, api이름이 맞지 않다..
        // 그래서 BDD Mockito을 이용
//        when(memberService.findById(1L)).thenReturn(Optional.of(member));
//        when(studyRepository.save(study)).thenReturn(study);

        // 위 코드를 아래처럼 바꿀수 있다. (BDDMockito package 이용)
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        //When
        studyService.createNewStudy(1L, study);

        //Then
        assertEquals(member, study.getOwner()); // 쥬피터꺼

        // 아래도 BDD style은 아님.. 그래서 BDDMockito 의 API로 변경하면..
//        verify(memberService, times(1)).notify(study);
        // 이게 BDD 스타일. should 안에는 아무것도 안넣을 수도 있음
        then(memberService).should( times(1)).notify(study);

        // 이것도 BDD로 변경해보면
//        verifyNoInteractions(memberService);
        then(memberService).shouldHaveNoMoreInteractions();

    }
}