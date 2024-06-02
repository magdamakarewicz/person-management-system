package com.enjoythecode.personservice.factory.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Retiree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RetireeSpecification implements SearchSpecification<Retiree> {

    private final GeneralPersonSpecification generalSpecification;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Specification<Person> createSpecification(Map<String, String> parameters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "yearsOfWork" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder, root,
                                key, value, Integer::parseInt);
                        case "pension" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder, root,
                                key, value, Double::parseDouble);
                    }
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
