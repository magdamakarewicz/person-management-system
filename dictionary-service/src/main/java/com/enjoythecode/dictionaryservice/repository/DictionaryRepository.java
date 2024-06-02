package com.enjoythecode.dictionaryservice.repository;

import com.enjoythecode.dictionaryservice.model.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    @Query("SELECT d FROM Dictionary d LEFT JOIN FETCH d.dictionaryValues WHERE d.id = ?1")
    Optional<Dictionary> findByIdWithDictionaryValues(Long id);

}
