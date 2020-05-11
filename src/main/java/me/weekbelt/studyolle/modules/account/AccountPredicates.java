package me.weekbelt.studyolle.modules.account;

import com.querydsl.core.types.Predicate;
import me.weekbelt.studyolle.modules.tag.Tag;
import me.weekbelt.studyolle.modules.zone.Zone;

import java.util.Set;

import static me.weekbelt.studyolle.modules.account.QAccount.account;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        return account.zones.any().in(zones)
                .and(account.tags.any().in(tags));
    }
}
