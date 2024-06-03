package com.enjoythecode.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.enjoythecode.userservice.model.AppRole;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

}
