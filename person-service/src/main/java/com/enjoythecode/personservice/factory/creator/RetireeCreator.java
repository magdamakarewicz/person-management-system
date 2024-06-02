package com.enjoythecode.personservice.factory.creator;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.CreateRetireeCommand;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Retiree;

@Service
@RequiredArgsConstructor
public class RetireeCreator implements PersonCreator {

    private final DictionaryServiceClient dictionaryServiceClient;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateRetireeCommand retireeCommand = modelMapper.map(createPersonCommand, CreateRetireeCommand.class);
        return new Retiree(
                dictionaryServiceClient
                        .getDictionaryValueByDictionaryIdAndName(1L, createPersonCommand.getType()).getId(),
                retireeCommand.getFirstName(),
                retireeCommand.getLastName(),
                retireeCommand.getPesel(),
                retireeCommand.getHeight(),
                retireeCommand.getWeight(),
                retireeCommand.getEmail(),
                retireeCommand.getPension(),
                retireeCommand.getYearsOfWork()
        );
    }

}
