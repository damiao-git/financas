package com.house.financas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUsuarioSenhaTemporariaRequest {

    @NotBlank(message = "A senha temporária é obrigatória")
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
    private String senhaTemporaria;
}
