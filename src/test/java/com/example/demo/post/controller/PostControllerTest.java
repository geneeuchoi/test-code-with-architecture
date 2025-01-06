package com.example.demo.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostControllerTest {

    @Test
    void 사용자는_게시물을_단건_조회_할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 123123123L)
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        testContainer.userRepository.save(user);

        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("hello")
                .writer(user)
                .createdAt(100L)
                .build());

        // when
        ResponseEntity<PostResponse> result =  testContainer.postController.getById(1L);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("hello");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("test");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(100L);
    }

    @Test
    void 사용자가_존재하지_않는_게시물을_조회할_경우_에러가_난다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 123123123L)
                .build();

        // when
        // then
        assertThatThrownBy(
                () -> testContainer.postController.getById(2L)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_게시물을_수정할_수_있다() {
     // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 123123123L)
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .build();

        testContainer.userRepository.save(user);
        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("hello")
                .writer(user)
                .createdAt(100L)
                .build());

        PostUpdate postUpdate = PostUpdate.builder()
                .content("배고프다")
                .build();

        // when
        ResponseEntity<PostResponse> result =  testContainer.postController.update(1L, postUpdate);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("배고프다");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("test");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(100L);
        assertThat(result.getBody().getModifiedAt()).isEqualTo(123123123L);
    }

}