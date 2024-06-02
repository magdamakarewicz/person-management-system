package com.enjoythecode.dictionaryservice.repository;

import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryValueRepository extends JpaRepository<DictionaryValue, Long> {

    @Query("SELECT dv FROM DictionaryValue dv JOIN dv.dictionary d WHERE d.id = ?1")
    List<DictionaryValue> findValuesByDictionaryId(Long dictionaryId);

    @Modifying
    @Query("DELETE FROM DictionaryValue dv WHERE dv.dictionary IS NULL")
    void deleteUnassignedValues();

    @Query("SELECT dv FROM DictionaryValue dv WHERE dv.dictionary.id = :dictionaryId AND dv.name = :valueName")
    Optional<DictionaryValue> findByDictionaryIdAndName(@Param("dictionaryId") Long dictionaryId,
                                                        @Param("valueName") String valueName);

}
