package com.enjoythecode.personservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.enjoythecode.personservice.command.CreatePersonFromCsvCommand;
import com.enjoythecode.personservice.exception.DataImportFromFileException;
import com.enjoythecode.personservice.factory.cteatorfromcsv.PersonFromCsvFactory;
import com.enjoythecode.personservice.model.ImportStatus;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.repository.PersonRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DataImportFromCsvService {

    private final PersonRepository personRepository;

    private final PersonFromCsvFactory personFromCsvFactory;

    private ImportStatus importStatus;

    private final ReentrantLock importLock = new ReentrantLock();

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Void> importPeopleFromCsvFile(MultipartFile file) {
        importStatus = new ImportStatus();
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (importLock.tryLock()) {
            try {
                if (file == null || file.isEmpty()) {
                    future.completeExceptionally(new DataImportFromFileException("File is empty or does not exist."));
                }
                importStatus.setInProgress(true);
                importStatus.setStartTime(LocalDateTime.now());
                Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
                AtomicLong importedCount = new AtomicLong(0);
                try {
                    lines
                            .skip(1)
                            .map(line -> line.split(","))
                            .filter(parameters -> parameters.length > 0)
                            .forEach(parameters -> {
                                String personType = parameters[0].trim();
                                CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand(personType, parameters);
                                Person person = personFromCsvFactory.create(command);
                                personRepository.save(person);
                                importStatus.setProcessedRows(importedCount.incrementAndGet());
                            });
                } catch (DataIntegrityViolationException e) {
                    future.completeExceptionally(new DataImportFromFileException("Duplicate entry. " +
                            "Constraint violation: UC_PERSON_PESEL"));
                }
            } catch (Exception e) {
                future.completeExceptionally(new DataImportFromFileException("Error during data import. " +
                        "Invalid file content. Message: " + e.getMessage()));
            } finally {
                if (!future.isCompletedExceptionally())
                    importStatus.setCompleted(true);
                importStatus.setInProgress(false);
                importStatus.setEndTime(LocalDateTime.now());
                future.complete(null);
                importLock.unlock();
            }
        } else {
            future.completeExceptionally(new DataImportFromFileException("Another import is already in progress."));
        }
        return future;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

}
