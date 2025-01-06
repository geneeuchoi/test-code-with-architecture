package com.example.demo.post.controller.response;

import com.example.demo.mock.*;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class PostCreateControllerTest {

    @Test
    void 사용자는_게시물을_작성할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 123123123L)
                .build();

       testContainer.userRepository.save(User.builder()
               .id(1L)
               .email("test@test.com")
               .nickname("test")
               .address("Seoul")
               .status(UserStatus.ACTIVE)
               .lastLoginAt(100L)
               .certificationCode("aaaaa-aaa-aaaa-aaaaa")
               .build());
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("hello")
                .build();

        // when
        ResponseEntity<PostResponse> result =  testContainer.postCreateController.create(postCreate);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("hello");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("test");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(123123123L);
    }
}
