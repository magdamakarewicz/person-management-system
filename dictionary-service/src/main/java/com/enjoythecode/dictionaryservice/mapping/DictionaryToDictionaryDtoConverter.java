package com.enjoythecode.dictionaryservice.mapping;

import com.enjoythecode.dictionaryservice.dto.DictionaryDto;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class DictionaryToDictionaryDtoConverter implements Converter<Dictionary, DictionaryDto> {

    @Override
    public DictionaryDto convert(MappingContext<Dictionary, DictionaryDto> mappingContext) {
        Dictionary source = mappingContext.getSource();
        return DictionaryDto.builder()
                .id(source.getId())
                .name(source.getName())
                .dictionaryValues(source.getDictionaryValues()
                        .stream()
                        .map(DictionaryValue::getName)
                        .toArray(String[]::new))
                .build();
    }

}
