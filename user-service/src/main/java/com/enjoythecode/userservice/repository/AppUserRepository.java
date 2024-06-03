package com.enjoythecode.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.enjoythecode.userservice.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.appRoles WHERE u.username = ?1")
    Optional<AppUser> findByUsernameWithRoles(String username);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.appRoles WHERE u.id = ?1")
    Optional<AppUser> findByIdWithRoles(Long id);

    @Query("SELECT u FROM AppUser u JOIN u.appRoles r WHERE r.id = ?1")
    List<AppUser> findUsersByAppRoleId(Long roleId);

}
