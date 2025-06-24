package io.ifyoudev.ifyoudevserver.core.v1.auth;

import java.util.Optional;

public interface RefreshTokenStorage {

    /**
     * 사용자의 리프레시 토큰을 저장 또는 갱신합니다.
     * 이미 토큰이 존재하는 사용자라면, 토큰을 갱신(교체)합니다. 갱신 후 이전 토큰은 저장되지 않으므로 주의해야합니다.
     * @param userUuid 사용자의 UUID
     * @param tokenValue 리프레시 토큰 값
     * @param expirationMills 만료 시간(밀리초 단위)
     */
    void update(String userUuid, String tokenValue);

    /**
     * 사용자의 리프레시 토큰을 조회합니다.
     * @param userUuid 조회할 사용자의 UUID
     * @return 저장된 리프레시 토큰 값
     */
    Optional<String> findByUserUuid(String userUuid);

    /**
     * 사용자의 리프레시 토큰을 삭제합니다.
     * @param userUuid 삭제할 사용자의 UUID
     * @return 삭제를 수행했다면 true, 삭제할 토큰이 존재하지 않으면 false
     */
    boolean deleteByUserUuid(String userUuid);
}
