package ua.edu.cdu.vu.event.notification.telegram.bot.redis.hash;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@RedisHash
public class UserStateHash {

    @Id
    private Long userId;

    private int flowId;
    private int stepId;

    private Map<String, String> data;
}
