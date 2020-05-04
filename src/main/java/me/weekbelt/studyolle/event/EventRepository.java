package me.weekbelt.studyolle.event;

import me.weekbelt.studyolle.domain.Event;
import me.weekbelt.studyolle.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStudyOrderByStartDateTime(Study study);
}
