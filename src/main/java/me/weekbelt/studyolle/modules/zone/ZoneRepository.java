package me.weekbelt.studyolle.modules.zone;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String city, String province);
}
