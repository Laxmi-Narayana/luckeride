package com.lucke.luckeride.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must contain 10 to 15 digits"
        )
        String phoneNumber
) {
}