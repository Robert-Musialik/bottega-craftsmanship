package com.vattenfall.emobility.infrastructure.jackson

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
import com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.PropertyAccessor.FIELD
import com.fasterxml.jackson.databind.MapperFeature.AUTO_DETECT_FIELDS
import com.fasterxml.jackson.databind.MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

val customObjectMapper: ObjectMapper = Jackson2ObjectMapperBuilder()
        .modules(ParameterNamesModule(PROPERTIES), Jdk8Module(), JavaTimeModule(), KotlinModule())
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .visibility(FIELD, ANY)
        .failOnUnknownProperties(false)
        .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
        .featuresToEnable(CAN_OVERRIDE_ACCESS_MODIFIERS, AUTO_DETECT_FIELDS)
        .serializationInclusion(NON_NULL)
        .build()

class CustomObjectMapperSupplier : ObjectMapperSupplier {
    override fun get(): ObjectMapper {
        return customObjectMapper
    }
}
