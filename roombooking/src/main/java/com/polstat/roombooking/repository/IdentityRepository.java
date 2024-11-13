package com.polstat.roombooking.repository;

import com.polstat.roombooking.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRepository extends JpaRepository<Identity, Long> {
}
