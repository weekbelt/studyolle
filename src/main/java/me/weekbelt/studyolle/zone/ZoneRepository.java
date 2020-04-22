package me.weekbelt.studyolle.zone;

import me.weekbelt.studyolle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String city, String province);
}
