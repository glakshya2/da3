package com.library.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("isbnValidator")
public class IsbnValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        if (value == null) {
            return; // Let @NotNull handle nulls if applicable
        }

        String isbn = value.toString().trim();

        // Basic ISBN-10 or ISBN-13 validation (numeric characters and length)
        // This is a simplified validation. More robust validation would include checksums.
        if (!isbn.matches("^(?:ISBN(?:-13)?:?)(?=[0-9]{13}$)([0-9]{3}-){2}[0-9]{3}[0-9X]$|^(?:ISBN(?:-10)?:?)(?=[0-9]{10}$|(?=[0-9]{9}X$))([0-9]{9}[0-9X])$")
                && !isbn.matches("^[0-9]{10}$|^[0-9]{13}$")) {
            FacesMessage message = new FacesMessage("Invalid ISBN format.",
                    "ISBN must be 10 or 13 digits, optionally with hyphens, and can end with X for ISBN-10.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
