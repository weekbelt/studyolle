package me.weekbelt.studyolle.event;

import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Enrollment;
import me.weekbelt.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount(Event event, Account account);
}
