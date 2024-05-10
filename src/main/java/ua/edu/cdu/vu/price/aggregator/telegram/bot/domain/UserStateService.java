package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import java.util.Optional;

public interface UserStateService {

    Optional<UserState> findUserState(long userId);

    boolean exists(long userId);

    void save(UserState userState);

    void delete(UserState userState);
}
