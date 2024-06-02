package com.enjoythecode.personservice.factory.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentSpecification implements SearchSpecification<Student> {

    private final GeneralPersonSpecification generalSpecification;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Specification<Person> createSpecification(Map<String, String> parameters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "universityNameId", "fieldOfStudyId" -> generalSpecification.addDictionaryValueCriteria
                                (predicates, criteriaBuilder, root, key, value);
                        case "enrollmentYear" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder, root,
                                key, value, Integer::parseInt);
                        case "scholarship" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder, root,
                                key, value, Double::parseDouble);
                    }
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
