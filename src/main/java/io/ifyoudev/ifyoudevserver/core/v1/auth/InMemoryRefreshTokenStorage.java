package io.ifyoudev.ifyoudevserver.core.v1.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryRefreshTokenStorage implements RefreshTokenStorage {

    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public void update(String userUuid, String tokenValue) {
        storage.put(userUuid, tokenValue);
    }

    @Override
    public Optional<String> findByUserUuid(String userUuid) {
        return Optional.ofNullable(storage.getOrDefault(userUuid, null));
    }

    @Override
    public boolean deleteByUserUuid(String userUuid) {
        if (storage.containsKey(userUuid)) {
            storage.remove(userUuid);
            return true;
        }
        return false;
    }
}
