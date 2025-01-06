package com.example.demo.post.service;

import com.example.demo.mock.*;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostServiceImplTest {

    private PostServiceImpl postServiceImpl;

    @BeforeEach
    void init() {
        FakePostRepository fakePostRepository = new FakePostRepository();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();

        this.postServiceImpl = PostServiceImpl.builder()
                .postRepository(fakePostRepository)
                .userRepository(fakeUserRepository)
                .clockHolder(new TestClockHolder(1231231L))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("test2@test.com")
                .nickname("test2")
                .address("Seoul")
                .certificationCode("aaaaa-aaa-aaaa-aaaaab")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build();

        fakeUserRepository.save(user1);
        fakeUserRepository.save(user2);

        fakePostRepository.save(Post.builder()
                        .id(1L)
                        .content("hi")
                        .createdAt(1231231L)
                        .modifiedAt(0L)
                        .writer(user1)
                .build());

        fakePostRepository.save(Post.builder()
                .id(2L)
                .content("hi")
                .createdAt(1231231L)
                .modifiedAt(0L)
                .writer(user2)
                .build());
    }

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        // given
        // when
        Post result = postServiceImpl.getById(1);

        // then
        assertThat(result.getContent()).isEqualTo("hi");
        assertThat(result.getWriter().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void postCreateDto_를_이용하여_게시물을_생성할_수_있다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("hello")
                .build();

        // when
        Post result = postServiceImpl.create(postCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getWriter().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getCreatedAt()).isEqualTo(1231231L);
    }

    @Test
    void postUpdateDto_를_이용하여_게시물을_수정할_수_있다() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("helloWorld")
                .build();

        // when
        postServiceImpl.update(1, postUpdate);

        // then
        Post post = postServiceImpl.getById(1);
        assertThat(post.getContent()).isEqualTo("helloWorld");
        assertThat(post.getCreatedAt()).isEqualTo(1231231L);
    }
}
