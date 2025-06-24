package io.ifyoudev.ifyoudevserver.core.v1.users.validator;

import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.validator.annotation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(email)) return true;
        return userRepository.findOneByEmail(email).isEmpty();
    }
}
