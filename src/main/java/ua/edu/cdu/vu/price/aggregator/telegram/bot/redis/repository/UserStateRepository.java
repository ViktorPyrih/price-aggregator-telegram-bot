package ua.edu.cdu.vu.event.notification.telegram.bot.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.edu.cdu.vu.event.notification.telegram.bot.redis.hash.UserStateHash;

@Repository
public interface UserStateRepository extends CrudRepository<UserStateHash, Long> {
}
