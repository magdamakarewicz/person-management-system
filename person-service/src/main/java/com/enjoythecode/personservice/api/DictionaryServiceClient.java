package com.enjoythecode.personservice.api;

import com.enjoythecode.personservice.dto.DictionarySimpleDto;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * This Feign client allows you to access dictionary data from the 'dictionary-service'.
 * The 'dictionary-service' provides dictionaries for various purposes.
 *
 * Dictionary Information:
 * - Dictionary with ID 1: 'types'
 * - Dictionary with ID 2: 'positions'
 * - Dictionary with ID 3: 'university names'
 * - Dictionary with ID 4: 'fields of study'
 */
@FeignClient(name = "dictionary-service", url = "http://localhost:8082")
public interface DictionaryServiceClient {

    /**
     * Retrieves a dictionary value by its ID.
     *
     * @param dictionaryValueId The ID of the dictionary value to retrieve.
     * @return A DictionaryValueSimpleDto representing the dictionary value.
     */
    @GetMapping("/api/dictionaryvalues/{dictionaryValueId}")
    DictionaryValueSimpleDto getDictionaryValueById(@PathVariable("dictionaryValueId") Long dictionaryValueId);

    /**
     * Retrieves a dictionary value by its name and by dictionary ID.
     *
     * @param dictionaryId The ID of the dictionary.
     * @param name The name of the dictionary value to retrieve.
     * @return A DictionaryValueSimpleDto representing the dictionary value.
     */
    @GetMapping("/api/dictionaryvalues/{dictionaryId}/value")
    DictionaryValueSimpleDto getDictionaryValueByDictionaryIdAndName(
            @PathVariable("dictionaryId") Long dictionaryId, @RequestParam String name);

    /**
     * Adds a new value to the 'types' dictionary (ID 1).
     *
     * @param name The name of the value to add to the dictionary.
     * @return A DictionarySimpleDto representing the updated 'types' dictionary.
     */
    @PostMapping("/api/dictionaries/1/values")
    DictionarySimpleDto addValueToTypeDictionary(@Valid @RequestParam String name);

}
