package eu.solidcraft.base

import eu.solidcraft.AppRunner
import eu.solidcraft.infrastructure.config.Profiles
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@TypeChecked
@SpringBootTest(classes = [AppRunner])
@ActiveProfiles([Profiles.INTEGRATION])
@Transactional
@Rollback
@AutoConfigureMockMvc
abstract class IntegrationSpec extends Specification {
    @Autowired
    MockMvc mockMvc
}
