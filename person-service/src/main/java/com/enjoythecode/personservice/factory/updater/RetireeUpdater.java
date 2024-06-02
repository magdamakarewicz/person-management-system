package com.enjoythecode.personservice.factory.updater;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.command.UpdateRetireeCommand;
import com.enjoythecode.personservice.exception.InvalidTypeException;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Retiree;
import com.enjoythecode.personservice.repository.RetireeRepository;

@Service
@RequiredArgsConstructor
public class RetireeUpdater implements PersonUpdater {

    private final RetireeRepository retireeRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateRetireeCommand retireeCommand = modelMapper.map(updatePersonCommand, UpdateRetireeCommand.class);
            Retiree retireeForUpdate = retireeRepository.findById(retireeCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            retireeForUpdate.setFirstName(retireeCommand.getFirstName());
            retireeForUpdate.setLastName(retireeCommand.getLastName());
            retireeForUpdate.setPesel(retireeCommand.getPesel());
            retireeForUpdate.setHeight(retireeCommand.getHeight());
            retireeForUpdate.setWeight(retireeCommand.getWeight());
            retireeForUpdate.setEmail(retireeCommand.getEmail());
            retireeForUpdate.setVersion(retireeCommand.getVersion());
            retireeForUpdate.setYearsOfWork(retireeCommand.getYearsOfWork());
            retireeForUpdate.setPension(retireeCommand.getPension());
            return retireeForUpdate;
        } catch (ClassCastException e) {
            throw new InvalidTypeException("The type in the request body does not match the entity type");
        }
    }

}
