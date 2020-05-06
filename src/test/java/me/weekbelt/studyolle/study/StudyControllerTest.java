package me.weekbelt.studyolle.study;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.WithAccount;
import me.weekbelt.studyolle.account.AccountRepository;
import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Study;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class StudyControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected StudyService studyService;
    @Autowired
    protected StudyRepository studyRepository;
    @Autowired
    protected AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 개설 폼 조회")
    public void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 개설 - 완료")
    public void createStudy_success() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"));

        Study study = studyRepository.findByPath("test-path");
        assertThat(study).isNotNull();
        Account account = accountRepository.findByNickname("joohyuk");
        assertThat(study.getManagers().contains(account));
    }

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 개설 - 실패")
    public void createStudy_fail() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        studyService.createNewStudy(study, joohyuk);

        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));

    }

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 조회")
    public void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        studyService.createNewStudy(study, joohyuk);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }
}