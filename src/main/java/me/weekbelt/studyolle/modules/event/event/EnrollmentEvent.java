package me.weekbelt.studyolle.modules.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.modules.event.Enrollment;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

    protected final Enrollment enrollment;

    protected final String message;
}
