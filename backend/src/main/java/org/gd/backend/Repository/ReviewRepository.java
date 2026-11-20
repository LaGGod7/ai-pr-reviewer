package org.gd.backend.repository;

import org.gd.backend.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByRepoFullNameAndPrNumberOrderByCreatedAtDesc(
            String repoFullName, int  prNumber
    );
}
