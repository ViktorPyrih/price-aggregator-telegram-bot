package ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.redis.hash.UserStateHash;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserStateMapper {

    UserState convertToDomain(UserStateHash hash);

    UserStateHash convertToHash(UserState userState);

}
