package emobility.roaming.base.pooling

import groovy.transform.CompileStatic
import spock.util.concurrent.PollingConditions

/**
 * This class gives you default PoolingConditions, that can be customized by system properties, so that
 * for example on your local machine an assertion fails after 1 sec of waiting, but on integration test in Azure
 * it fails after 10 sec (shared CI workers tend to be slow).
 *
 * Use like this:
 *    WAIT.eventually {
 *          exampleKafkaListener.getReceivedValues(KEY) == [DATA]
 *    }
 *
 * And if you want to wait longer/shorter on a given environment set System property of tests.polling.timeout.multiplier
 *
 * Kudos to Bartek Wojtkiewicz for the idea & implementation
 */
@CompileStatic
class PredefinedPollingConditions {
    static final PollingConditions SHORT_WAIT = new PollingConditions(timeout: timout(DEFAULT_SHORT))
    static final PollingConditions WAIT = new PollingConditions(timeout: timout(DEFAULT_MEDIUM))
    static final PollingConditions LONG_WAIT = new PollingConditions(timeout: timout(DEFAULT_LONG))
    static final PollingConditions SHORT_WAIT_WITH_INITIAL_DELAY = new PollingConditions(timeout: timout(DEFAULT_SHORT) + 1, initialDelay: timout(DEFAULT_SHORT))

    private static final String MULTIPLIER_PROPERTY = "tests.polling.timeout.multiplier"
    private static final int DEFAULT_SHORT = 1
    private static final int DEFAULT_MEDIUM = 10
    private static final int DEFAULT_LONG = 30

    private static int timout(int defaultSeconds) {
        return System.getProperty(MULTIPLIER_PROPERTY) == null
                ? defaultSeconds
                : Integer.valueOf(System.getProperty(MULTIPLIER_PROPERTY)) * defaultSeconds
    }
}
