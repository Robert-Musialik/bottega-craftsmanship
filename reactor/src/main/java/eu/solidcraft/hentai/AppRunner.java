package eu.solidcraft.hentai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.blockhound.BlockHound;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@EnableWebFlux
@Slf4j
public class AppRunner {
    public static void main(String[] args) {
        //To see the line with error in debug, you can use
        //either
//         Hooks.onOperatorDebug();
        //this is not for production, it's very slow but bullet proof. Good for manual test
        //or ReactorDebugAgent which has only 10% performance impact
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        //optionally you can also use
        BlockHound.install();
        //to verify you do not block in reactor thread
        SpringApplication.run(AppRunner.class, args);
    }


//    /**
//     * When you uncomment this event listener, and you added debug info,
//     * notice how the error logs actually points you out to the source of the error
//     *
//     * Error has been observed at the following site(s):
//     * 	|_ Flux.single ⇢ at eu.solidcraft.hentai.AppRunner.go(AppRunner.java:43)
//     */
//    @EventListener(ApplicationReadyEvent.class)
//    public void go() throws Exception {
//        Flux.range(0, 5)
//                .single()
//                .block();
//    }

}
