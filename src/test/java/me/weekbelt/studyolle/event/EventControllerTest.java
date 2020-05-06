package me.weekbelt.studyolle.event;

import me.weekbelt.studyolle.WithAccount;
import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Event;
import me.weekbelt.studyolle.domain.EventType;
import me.weekbelt.studyolle.domain.Study;
import me.weekbelt.studyolle.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends StudyControllerTest {

    @Autowired
    EventService eventService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("joohyuk")
    @Test
    public void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account weekbelt = createAccount("weekbelt");
        Study study = createStudy("test-study", weekbelt);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, weekbelt);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));


        Account joohyuk = accountRepository.findByNickname("joohyuk");
        isAccepted(joohyuk, event);
    }

    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("joohyuk")
    @Test
    public void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account weekbelt = createAccount("weekbelt");
        Study study = createStudy("test-study", weekbelt);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, weekbelt);

        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));


        Account joohyuk = accountRepository.findByNickname("joohyuk");
        isNotAccepted(joohyuk, event);
    }

    @DisplayName("선착순 모임에 참가 신청 - 취소")
    @WithAccount("joohyuk")
    @Test
    public void cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account joohyuk = accountRepository.findByNickname("joohyuk");
        Account weekbelt = createAccount("weekbelt");
        Account may = createAccount("may");
        Study study = createStudy("test-study", weekbelt);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, weekbelt);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, joohyuk);
        eventService.newEnrollment(event, weekbelt);

        isAccepted(may, event);
        isAccepted(joohyuk, event);
        isNotAccepted(weekbelt, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(weekbelt, event);
        assertThat(enrollmentRepository.findByEventAndAccount(event, joohyuk)).isNull();
    }

    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("joohyuk")
    @Test
    public void newEnrollment_to_CONFIRMATIVE_event_not_accepted() throws Exception {
        Account weekbelt = createAccount("weekbelt");
        Study study = createStudy("test-study", weekbelt);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, weekbelt);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account joohyuk = accountRepository.findByNickname("joohyuk");
        isNotAccepted(joohyuk, event);
    }

    private void isAccepted(Account joohyuk, Event event) {
        assertThat(enrollmentRepository.findByEventAndAccount(event, joohyuk).isAccepted()).isTrue();
    }

    private void isNotAccepted(Account joohyuk, Event event) {
        assertThat(enrollmentRepository.findByEventAndAccount(event, joohyuk).isAccepted()).isFalse();
    }

    public Account createAccount(String nickname) {
        Account account = new Account();
        account.setNickname(nickname);
        account.setEmail(nickname + "@email.com");
        accountRepository.save(account);
        return account;
    }

    public Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }
}