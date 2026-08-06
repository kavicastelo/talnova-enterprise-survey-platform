package com.talnova.tesp.configservice.validation;

import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SupportedLocalesValidator implements ConstraintValidator<ValidSupportedLocales, ProjectCreateDTO> {

    @Override
    public boolean isValid(ProjectCreateDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (dto.getDefaultLocale() == null || dto.getSupportedLocales() == null) {
            return true;
        }

        return dto.getSupportedLocales().contains(dto.getDefaultLocale());
    }
}
