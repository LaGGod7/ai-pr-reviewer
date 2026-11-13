package org.gd.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

@Entity
@Getter @Setter
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_full_name", nullable = false)
    private String repoFullName;

    @Getter
    @Column(name = "pr_number",nullable = false)
    private int prNumber;



    @Column(name = "pr_title")
    private String prTitle;

    @Getter
    @Column(name = "pr_url")
    private String prUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "files_reviewed", nullable = false)
    private int filesReviewed;

    @Column(nullable = false)
    private boolean truncated;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FindingEntity> findings = new ArrayList<>();

    public void addFinding(String file, Integer line, String severity,
                           String message, String suggestion) {
        findings.add(new FindingEntity(this, file, line, severity, message, suggestion));
    }

    public long getId() {
        return id;
    }




    // JPA needs an empty constructor. Hibernate uses it when it loads rows.
    protected ReviewEntity() {
    }
    public ReviewEntity(String repoFullName, int prNumber, String prTitle, String prUrl,
                        String summary, int filesReviewed, boolean truncated) {
        this.repoFullName = repoFullName;
        this.prNumber = prNumber;
        this.prTitle = prTitle;
        this.prUrl = prUrl;
        this.summary = summary;
        this.filesReviewed = filesReviewed;
        this.truncated = truncated;
        this.createdAt = Instant.now();
    }


}
