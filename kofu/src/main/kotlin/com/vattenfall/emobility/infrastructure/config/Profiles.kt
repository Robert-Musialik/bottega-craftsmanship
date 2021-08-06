package com.vattenfall.emobility.infrastructure.config

object Profiles {
    /**
     * Profile used for any environment to run microservice in production mode
     */
    const val PROD = "prod"

    /**
     * Profile used for development environment in k8s
     */
    const val DEV = "dev"

    /**
     * Profile used for tests environment in k8s
     */
    const val TEST = "test"

    /**
     * Profile used for integration tests
     */
    const val INTEGRATION_TEST = "integration-test"

    /**
     * Profile used for running on local machine
     */
    const val LOCAL = "local"

    /**
     * Any profile except integration test
     */
    const val NOT_INTEGRATION_TEST = "!$INTEGRATION_TEST"

}
