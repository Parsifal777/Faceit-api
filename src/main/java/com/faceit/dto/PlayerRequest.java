package com.faceit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record PlayerRequest(
        @NotBlank(message = "Nickname is required")
        @Size(min = 3, max = 50, message = "Nickname must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname can only contain letters, numbers and underscore")
        String nickname,
        Integer teamId
) {}
