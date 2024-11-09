package com.polstat.roombooking.repository;

import com.polstat.roombooking.entity.Role;
import com.polstat.roombooking.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
