package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {

    @Test
    public void Post로_응답을_생성할_수_있다() {
        // given
        User writer = User.builder()
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .status(UserStatus.ACTIVE)
                .build();

        Post post = Post.builder()
                .content("helloWorld")
                .writer(writer)
                .build();
        // when
        PostResponse postResponse = PostResponse.from(post);

        // then
        assertThat(postResponse.getContent()).isEqualTo("helloWorld");
        assertThat(postResponse.getWriter().getEmail()).isEqualTo("test@test.com");
        assertThat(postResponse.getWriter().getNickname()).isEqualTo("test");
        assertThat(postResponse.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


}