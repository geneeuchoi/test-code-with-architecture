package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void User는_UserCreate_객체로_생성할_수_있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .build();
        // when
        User user = User.from(userCreate, new TestUuidHolder("aaaa-aaa-aaa-aaaa"));

        // then
        assertThat(user.getId()).isNull(); // wrapper 클래스. 초기화 되지 않아서 null임(jpa 엔티티가 아니니까)
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("test");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");
        // 윗 부분을 어떻게 생성할 것인가?
        // 바로바로 의존성 역전으로

    }

    @Test
    public void User는_UserUpdate_객체로_데이터를_업데이트_할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("test2")
                .address("Seoul2")
                .build();
        // when
        user = user.update(userUpdate);

        // then
        assertThat(user.getId()).isEqualTo(1L); // wrapper 클래스. 초기화 되지 않아서 null임(jpa 엔티티가 아니니까)
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("test2");
        assertThat(user.getAddress()).isEqualTo("Seoul2");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaa-aaa-aaaa-aaaaa");

    }

    @Test
    public void User는_로그인을_할_수_있고_로그인시_마지막_로그인_시간이_변경된다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        // when
        user = user.login(new TestClockHolder(1234556667L));

        // then
        assertThat(user.getLastLoginAt()).isEqualTo(1234556667L);
    }

    @Test
    public void User는_유효한_인증_코드로_계정을_활성화_할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        // when
        user = user.certificate("aaaaa-aaa-aaaa-aaaaa");

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void User는_잘못된_인증_코드로_계정을_활성화_하려하면_에러를_던진다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        // when
        // then
        assertThatThrownBy( () -> user.certificate("aaaaa-aaa-aaaa-aaaaab"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }



}