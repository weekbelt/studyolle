package me.weekbelt.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.weekbelt.studyolle.WithAccount;
import me.weekbelt.studyolle.account.AccountRepository;
import me.weekbelt.studyolle.account.AccountService;
import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Tag;
import me.weekbelt.studyolle.domain.Zone;
import me.weekbelt.studyolle.settings.form.TagForm;
import me.weekbelt.studyolle.settings.form.ZoneForm;
import me.weekbelt.studyolle.tag.TagRepository;
import me.weekbelt.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static me.weekbelt.studyolle.settings.SettingsController.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;
    // 추가
    @Autowired ZoneRepository zoneRepository;

    // 추가
    private Zone testZone = Zone.builder()
            .city("test").localNameOfCity("테스트시").province("테스트주")
            .build();

    // 추가
    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
        // 추가
        zoneRepository.deleteAll();
    }

    @WithAccount("joohyuk")
    @DisplayName("프로필 수정 폼")
    @Test
    public void updateProfileForm() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("joohyuk")
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @Test
    public void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"))
        ;
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        assertThat(joohyuk.getBio()).isEqualTo(bio);
    }

    @WithAccount("joohyuk")
    @DisplayName("프로필 수정 하기 - 입력값 에러")
    @Test
    public void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 길게 소개를 수정하는 경우";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        assertThat(joohyuk.getBio()).isNull();
    }

    @WithAccount("joohyuk")
    @DisplayName("패스워드 수정 폼")
    @Test
    public void updatePassword_form() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("joohyuk")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    public void updatePassword_success() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        assertTrue(passwordEncoder.matches("12345678", joohyuk.getPassword()));
    }

    @WithAccount("joohyuk")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    public void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "111111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

    }

    @WithAccount("joohyuk")
    @DisplayName("닉네임 수정 폼")
    @Test
    public void updateAccountForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));

    }

    @WithAccount("joohyuk")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    public void updateAccount_success() throws Exception {
        String newNickname = "weekbelt";
        mockMvc.perform(post("/settings/account")
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertThat(accountRepository.findByNickname(newNickname)).isNotNull();
    }

    @WithAccount("joohyuk")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    public void updateTagForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("joohyuk")
    @DisplayName("계정에 태그 추가")
    @Test
    public void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag").get();
        assertThat(newTag).isNotNull();
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        // 만약 @Transactional이 없다면 account는 detached 상태
        assertThat(joohyuk.getTags().contains(newTag))
                .isTrue();
    }

    @WithAccount("joohyuk")
    @DisplayName("계정에 태그 삭제")
    @Test
    public void removeTag() throws Exception {
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(joohyuk, newTag);

        assertThat(joohyuk.getTags().contains(newTag)).isTrue();

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertThat(joohyuk.getTags().contains(newTag)).isFalse();
    }

    @WithAccount("joohyuk")
    @DisplayName("계정의 지역 정보 수정 폼")
    @Test
    public void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @WithAccount("joohyuk")
    @DisplayName("계정의 지역 정보 추가")
    @Test
    public void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(),
                testZone.getProvince());
        assertThat(joohyuk.getZones().contains(zone)).isTrue();
    }

    @WithAccount("joohyuk")
    @DisplayName("계정의 지역 정보 삭제")
    @Test
    public void removeZone() throws Exception {
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(),
                testZone.getProvince());
        accountService.addZone(joohyuk, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

                assertThat(joohyuk.getZones().contains(zone)).isFalse();
    }
}
