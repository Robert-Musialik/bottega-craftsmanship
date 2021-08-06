package com.vattenfall.emobility

import com.vattenfall.emobility.example.exampleBeans
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class AppRunner

fun main(args: Array<String>) {
	runApplication<AppRunner>(*args)
}

//Ugly hack to register beans due to https://github.com/spring-projects/spring-boot/issues/8115
//see also https://stackoverflow.com/questions/45935931/how-to-use-functional-bean-definition-kotlin-dsl-with-spring-boot-and-spring-w/46033685#46033685
internal class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
	override fun initialize(context: GenericApplicationContext) =
			exampleBeans().initialize(context)
}
