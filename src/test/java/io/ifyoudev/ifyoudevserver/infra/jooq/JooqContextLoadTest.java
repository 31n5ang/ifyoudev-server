package io.ifyoudev.ifyoudevserver.infra.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@JooqTest
public class JooqContextLoadTest {

    @Autowired
    DSLContext dslContext;

    @Test
    @DisplayName("jOOQ DSLContext가 올바르게 로드됐는지 테스트")
    void dslContextLoadsCorrectly() {
        log.info("DSLContext Null 확인..");
        assertThat(dslContext).isNotNull();
        log.info("DSLContext 주입 성공");

        log.info("SELECT 1 쿼리 실행 시도..");
        Integer result = dslContext.selectOne().fetchOneInto(Integer.class);
        assertThat(result).isEqualTo(1);
        log.info("jOOQ DSLContext 로드 성공");
    }
}
