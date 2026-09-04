package com.example.aispringboot.DTO.middle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityUserDTO {
    private Long userId;
    private Byte userType;
}
