package eu.solidcraft.hentai.user;

import eu.solidcraft.hentai.infrastructure.security.CurrentUserGetter;
import reactor.core.publisher.Mono;

public class UserFacade {
    public Mono<String> getCurrentUserName() {
        return CurrentUserGetter.getCurrentUserName();
    }
}
