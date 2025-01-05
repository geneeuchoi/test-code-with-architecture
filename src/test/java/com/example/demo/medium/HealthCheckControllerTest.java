package com.example.demo.medium;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // MockMvc는 Spring Framework의 테스트 도구로,
    // 컨트롤러의 HTTP 요청과 응답을 실제 서블릿 컨테이너를 띄우지 않고도
    // 모의(Mocking) 환경에서 테스트할 수 있도록 해줍니다.
    // 이를 통해 애플리케이션의 웹 계층(특히 컨트롤러)에 대해
    // 효율적이고 빠르게 단위 테스트를 수행할 수 있습니다.

    // MockMvc는 실제 서버를 띄우지 않고
    // 애플리케이션의 서블릿 컨텍스트와 통합된 형태로 컨트롤러를 테스트합니다.
    // 이를 통해 애플리케이션의 HTTP 요청/응답 흐름을 검증할 수 있습니다.

    @Test
    void 헬스_체크_응답이_200으로_내려온다() throws Exception {
        mockMvc.perform(get("/health_check.html"))
                .andExpect(status().isOk());
    }
}