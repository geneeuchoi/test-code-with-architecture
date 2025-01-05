package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    public void PostCreate로_게시물을_만들_수_있다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("helloWorld")
                .build();

        User writer = User.builder()
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .status(UserStatus.ACTIVE)
                .build();

        // when
        Post post = Post.from(writer, postCreate, new TestClockHolder(1234L));

        // then
        assertThat(post.getContent()).isEqualTo("helloWorld");
        assertThat(post.getCreatedAt()).isEqualTo(1234L);
        assertThat(post.getWriter().getEmail()).isEqualTo("test@test.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("test");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaa-aaa-aaaa-aaaaa");

    }

    @Test
    public void PostUpdate로_게시물을_수정_할_수_있다() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("helloWorld re")
                .build();

        User writer = User.builder()
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .status(UserStatus.ACTIVE)
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("hi")
                .createdAt(1231231L)
                .modifiedAt(0L)
                .writer(writer)
                .build();

        // when
        post = post.update(postUpdate, new TestClockHolder(1234L));

        // then
        assertThat(post.getContent()).isEqualTo("helloWorld re");
        assertThat(post.getCreatedAt()).isEqualTo(1231231L);
        assertThat(post.getModifiedAt()).isEqualTo(1234L);

    }


}