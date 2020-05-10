package me.weekbelt.studyolle.modules.study;

import me.weekbelt.studyolle.infra.MockMvcTest;
import me.weekbelt.studyolle.modules.account.AccountFactory;
import me.weekbelt.studyolle.modules.account.WithAccount;
import me.weekbelt.studyolle.modules.account.AccountRepository;
import me.weekbelt.studyolle.modules.account.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudyControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected StudyService studyService;
    @Autowired protected StudyRepository studyRepository;
    @Autowired protected AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired StudyFactory studyFactory;

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

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 가입")
    void joinStudy() throws Exception {
        Account weekbelt = accountFactory.createAccount("weekbelt");
        Study study = studyFactory.createStudy("test-study", weekbelt);

        mockMvc.perform(get("/study/" + study.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        assertThat(study.getMembers().contains(joohyuk)).isTrue();
    }

    @Test
    @WithAccount("joohyuk")
    @DisplayName("스터디 탈퇴")
    void leaveStudy() throws Exception {
        Account weekbelt = accountFactory.createAccount("weekbelt");
        Study study = studyFactory.createStudy("test-study", weekbelt);
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        studyService.addMember(study, joohyuk);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        assertThat(study.getMembers().contains(joohyuk)).isFalse();
    }


}