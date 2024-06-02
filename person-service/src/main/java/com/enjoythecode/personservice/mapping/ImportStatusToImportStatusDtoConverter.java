package com.enjoythecode.personservice.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.dto.ImportStatusDto;
import com.enjoythecode.personservice.model.ImportStatus;

@Service
public class ImportStatusToImportStatusDtoConverter implements Converter<ImportStatus, ImportStatusDto> {

    @Override
    public ImportStatusDto convert(MappingContext<ImportStatus, ImportStatusDto> mappingContext) {
        ImportStatus source = mappingContext.getSource();
        String status = source.isCompleted() ? "Import completed." :
                (source.isInProgress() ? "Import is in progress." : "Import has not started yet.");
        return ImportStatusDto.builder()
                .startTime(source.getStartTime())
                .status(status)
                .endTime(source.getEndTime())
                .processedRows(source.getProcessedRows())
                .build();
    }

}
