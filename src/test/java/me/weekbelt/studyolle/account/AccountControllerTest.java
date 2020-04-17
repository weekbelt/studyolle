package me.weekbelt.studyolle.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    public void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "joohyuk")
                .param("email", "email...")
                .param("password", "12345")
                .with(csrf()))      // POST 요청시 CSRF값을 비교하여 같으면 요청을 처리하고 다르면 403에러가 발생한다.
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
        ;
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    public void signUpSubmit_with_correct_input() throws Exception {
        // 회원 가입 요청이 제대로 작동하는지 화인
        mockMvc.perform(post("/sign-up")
                .param("nickname", "joohyuk")
                .param("email", "vfrvfr4207@hanmail.net")
                .param("password", "12345678")
                .with(csrf()))      // POST 요청시 CSRF값을 비교하여 같으면 요청을 처리하고 다르면 403에러가 발생한다.
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;

        // 가입 된 회원이 존재하는지 확인
        assertThat(accountRepository.existsByEmail("vfrvfr4207@hanmail.net")).isTrue();
        // 회원가입시 JavaMailSender 통해 SimpleMailMessage 호출되는지
        then(javaMailSender).should().send(any(SimpleMailMessage.class));

    }

}