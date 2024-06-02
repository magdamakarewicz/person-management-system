package com.enjoythecode.personservice.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper createModelMapper(Set<Converter> converters) {
        ModelMapper modelMapper = new ModelMapper();
        converters.forEach(modelMapper::addConverter);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

}
