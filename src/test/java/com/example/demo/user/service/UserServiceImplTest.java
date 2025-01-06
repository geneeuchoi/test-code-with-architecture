package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userServiceImpl = UserServiceImpl.builder()
                .uuidHolder(new TestUuidHolder("aaaa-aaa-aaa-aaaa"))
                .clockHolder(new TestClockHolder(1234))
                .userRepository(fakeUserRepository)
                .certificationService(new CertificationService(fakeMailSender))
                .build();
        fakeUserRepository.save(User.builder()
                        .id(1L)
                        .email("test@test.com")
                        .nickname("test")
                        .address("Seoul")
                        .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                        .status(UserStatus.ACTIVE)
                        .lastLoginAt(0L)
                .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("test2@test.com")
                .nickname("test2")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());
    }


    @Test
    void getByEmail_은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "test@test.com";

        // when
        User result = userServiceImpl.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("test");

    }

    @Test
    void getByEmail_은_PENDING_상태인_유저는_찾아올_수_없다() {
        // given
        String email = "test2@test.com";

        // when
        // then
        assertThatThrownBy(() -> {
            userServiceImpl.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_는_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        User result = userServiceImpl.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("test");

    }

    @Test
    void getById_는_PENDING_상태인_유저는_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            User result = userServiceImpl.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void UserCreateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("test3@test.com")
                .address("Seoul")
                .nickname("test3")
                .build();

        // when
        User result = userServiceImpl.create(userCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");

    }

    @Test
    void UserUpdateDto_를_이용하여_유저를_수정할_수_있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Gwangmyeng")
                .nickname("test3")
                .build();

        // when
        userServiceImpl.update(1, userUpdate);

        // then
        User result = userServiceImpl.getById(1);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Gwangmyeng");
        assertThat(result.getNickname()).isEqualTo("test3");

    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userServiceImpl.login(1);

        // then
        User result = userServiceImpl.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0L);
        assertThat(result.getLastLoginAt()).isEqualTo(1234);

    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() {
        // given
        // when
        userServiceImpl.verifyEmail(2, "aaaaa-aaa-aaaa-aaaaab");

        // then
        User result = userServiceImpl.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);

    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(()-> {
            userServiceImpl.verifyEmail(2, "aaaaa-aaa-aaaa-aaaabc");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);

    }



}