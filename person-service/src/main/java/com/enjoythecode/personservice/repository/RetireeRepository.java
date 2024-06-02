package com.enjoythecode.personservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.enjoythecode.personservice.model.Retiree;

public interface RetireeRepository extends JpaRepository<Retiree, Long> {

}
