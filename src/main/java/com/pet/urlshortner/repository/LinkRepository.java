package com.pet.urlshortner.repository;

import com.pet.urlshortner.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    boolean existsByShortCode(String shortCode);
    Optional<Link> findByShortCode(String shortCode);
}
