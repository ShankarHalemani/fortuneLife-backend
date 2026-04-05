package com.techlabs.app.repository;

import com.techlabs.app.entity.InsuranceScheme;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemeRepository extends JpaRepository<InsuranceScheme, Long> {
    Optional<InsuranceScheme> findBySchemeName(String schemeName);

    @Query("SELECT s FROM InsuranceScheme s WHERE " +
            "LOWER(s.schemeName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<InsuranceScheme> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
