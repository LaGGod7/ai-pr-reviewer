package org.gd.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "findings")
@Getter
public class FindingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    @Column(name = "file")
    private String file;

    @Column(name = "line")
    private Integer line;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    protected FindingEntity() {
    }

    public FindingEntity(ReviewEntity review, String file, Integer line,
                         String severity, String message, String suggestion) {
        this.review = review;
        this.file = file;
        this.line = line;
        this.severity = severity;
        this.message = message;
        this.suggestion = suggestion;
    }

}
