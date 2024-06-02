package com.enjoythecode.personservice.mapping;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.dto.EmployeePositionDto;
import com.enjoythecode.personservice.model.EmployeePosition;

@Service
@RequiredArgsConstructor
public class EmployeePositionToEmployeePositionDtoConverter implements Converter<EmployeePosition, EmployeePositionDto> {

    private final DictionaryServiceClient dictionaryServiceClient;

    @Override
    public EmployeePositionDto convert(MappingContext<EmployeePosition, EmployeePositionDto> mappingContext) {
        EmployeePosition source = mappingContext.getSource();
        return EmployeePositionDto.builder()
                .position(dictionaryServiceClient.getDictionaryValueById(source.getPositionId()).getName())
                .startDate(source.getStartDate())
                .endDate(source.getEndDate())
                .salary(source.getSalary())
                .build();
    }

}
