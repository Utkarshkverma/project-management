package com.vermau2k01.project_management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be minimum 8 characters")
    private String password;
}
