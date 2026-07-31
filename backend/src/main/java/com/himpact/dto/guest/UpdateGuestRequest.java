package com.himpact.dto.guest;

import com.himpact.entity.GuestStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateGuestRequest(
        @Size(max = 255)
        String fullName,

        String mobile,

        @Email
        String email,

        Integer uploadLimit,

        GuestStatus status
) {}
