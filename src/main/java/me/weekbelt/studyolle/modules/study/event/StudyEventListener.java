package me.weekbelt.studyolle.modules.study.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.weekbelt.studyolle.infra.config.AppProperties;
import me.weekbelt.studyolle.infra.mail.EmailMessage;
import me.weekbelt.studyolle.infra.mail.EmailService;
import me.weekbelt.studyolle.modules.account.Account;
import me.weekbelt.studyolle.modules.account.AccountPredicates;
import me.weekbelt.studyolle.modules.account.AccountRepository;
import me.weekbelt.studyolle.modules.notification.Notification;
import me.weekbelt.studyolle.modules.notification.NotificationRepository;
import me.weekbelt.studyolle.modules.notification.NotificationType;
import me.weekbelt.studyolle.modules.study.Study;
import me.weekbelt.studyolle.modules.study.StudyRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Slf4j
@Async
@Transactional
@Component
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        // Tags와 Zones를 참조할수 있는 Study를 가져왔다.
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()){
                sendStudyCreatedEmail(study, account);
            }

            if (account.isStudyCreatedByWeb()) {
                saveStudyCreatedNotification(study, account);
            }
        });
    }

    private void sendStudyCreatedEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkname", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생겼습니다.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디올래, '" + study.getTitle() + "' 스터디가 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(study.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }
}
