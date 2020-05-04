package me.weekbelt.studyolle.event;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Event;
import me.weekbelt.studyolle.domain.Study;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 모임이 없습니다."));
    }
}
