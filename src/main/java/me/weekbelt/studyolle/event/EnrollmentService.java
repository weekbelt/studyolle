package me.weekbelt.studyolle.event;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.domain.Enrollment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment findEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new IllegalArgumentException("찾는 참가자가 없습니다."));
    }
}
