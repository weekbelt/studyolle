package me.weekbelt.studyolle.modules.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.weekbelt.studyolle.modules.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;
@NamedEntityGraph(
        name = "Enrollment.withEventAndStudy",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name = "study", attributeNodes = @NamedAttributeNode("study"))
)
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
