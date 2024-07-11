package ru.practicum.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import ru.practicum.model.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
@DynamicInsert
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    @Column(length = 7000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;

    private String location;

    @ColumnDefault("false")
    private Boolean paid;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer participantLimit;

    private LocalDateTime publishedOn;

    @ColumnDefault("true")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventStatus state;

    @Column(length = 120, nullable = false)
    private String title;

    @ColumnDefault("0")
    private Long views;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private List<Comment> comments;
}
