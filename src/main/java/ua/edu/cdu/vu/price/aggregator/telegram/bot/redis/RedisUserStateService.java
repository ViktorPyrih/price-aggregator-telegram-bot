package ua.edu.cdu.vu.price.aggregator.telegram.bot.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserStateService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.UserStateMapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.redis.hash.UserStateHash;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.redis.repository.UserStateRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUserStateService implements UserStateService {

    private final UserStateRepository userStateRepository;
    private final UserStateMapper userStateMapper;

    public Optional<UserState> findUserState(long userId) {
        return userStateRepository.findById(userId)
                .map(userStateMapper::convertToDomain);
    }

    @Override
    public boolean exists(long userId) {
        return userStateRepository.existsById(userId);
    }

    public void save(UserState userState) {
        UserStateHash userStateHash = userStateMapper.convertToHash(userState);
        userStateRepository.save(userStateHash);
    }

    public void delete(UserState userState) {
        userStateRepository.deleteById(userState.userId());
    }
}
