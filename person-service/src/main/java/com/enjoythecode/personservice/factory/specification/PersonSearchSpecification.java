package com.enjoythecode.personservice.factory.specification;

import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.model.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonSearchSpecification {

    private final SearchSpecification<Person> generalSpecification;

    private final Map<String, SearchSpecification<? extends Person>> typeSpecifications;

    private final DictionaryServiceClient dictionaryServiceClient;

    public PersonSearchSpecification(SearchSpecification<Person> generalSpecification,
                                     Set<SearchSpecification<? extends Person>> typeSpecifications,
                                     DictionaryServiceClient dictionaryServiceClient) {
        this.generalSpecification = generalSpecification;
        this.typeSpecifications = typeSpecifications.stream()
                .collect(Collectors.toMap(SearchSpecification::getType, Function.identity()));
        this.dictionaryServiceClient = dictionaryServiceClient;
    }

    public Specification<Person> filterByCriteria(Map<String, String> parameters) {
        String filteredType = parameters.get("typeId");
        Specification<Person> generalSpec = generalSpecification.createSpecification(parameters);
        if (filteredType != null) {
            Long typeId = Long.valueOf(parameters.get("typeId"));
            String type = dictionaryServiceClient.getDictionaryValueById(typeId).getName();
            SearchSpecification<? extends Person> typeSpec = typeSpecifications.get(type);
            if (typeSpec != null) {
                Specification<Person> typeSpecification = typeSpec.createSpecification(parameters);
                return generalSpec.and(typeSpecification);
            }
        }
        return generalSpec;
    }

}