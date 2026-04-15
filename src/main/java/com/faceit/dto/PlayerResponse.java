package com.faceit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private Integer playerId;
    private String nickname;
    private Integer teamId;
    private String teamName;
}
