package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.ClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.CertificationService;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된_채_전달_받을_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("test");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);

//        UserController userController = UserController.builder()
//                .userReadService(new UserReadService() {
//                    @Override
//                    public User getByEmail(String email) {
//                        return null;
//                    }
//
//                    @Override
//                    public User getById(long id) {
//                        return User.builder()
//                                .email("test@test.com")
//                                .nickname("test")
//                                .address("Seoul")
//                                .status(UserStatus.ACTIVE)
//                                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
//                                .build();
//                    }
//                })
//                .build();
//        // when
//        ResponseEntity<UserResponse> result = userController.getUserById(1);
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().getId()).isEqualTo(1);
//        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
//        assertThat(result.getBody().getNickname()).isEqualTo("test");
//        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // then
        assertThatThrownBy(() -> {
            testContainer.userController.getUserById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaaa-aaa-aaaa-aaaaa");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .lastLoginAt(100L)
                .build());

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.userController.verifyEmail(1, "aaaaa-aaa-aaaa-aaaaab");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);

    }

    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 321313131L)
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .lastLoginAt(321313131L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("test@test.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("test");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(321313131L);
        assertThat(result.getBody().getAddress()).isEqualTo("Seoul");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .lastLoginAt(100L)
                .build());
        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.updateMyInfo("test@test.com", UserUpdate.builder()
                .address("Seoul2")
                .nickname("test2")
                .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("test2");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
        assertThat(result.getBody().getAddress()).isEqualTo("Seoul2");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

}