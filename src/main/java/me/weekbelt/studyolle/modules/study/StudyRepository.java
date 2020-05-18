package me.weekbelt.studyolle.modules.study;

import me.weekbelt.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {
    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    Optional<Study> findStudyOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean checked);

}
