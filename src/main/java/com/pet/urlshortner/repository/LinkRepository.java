package com.pet.urlshortner.repository;

import com.pet.urlshortner.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
    boolean existsByShortCode(String shortCode);
}
