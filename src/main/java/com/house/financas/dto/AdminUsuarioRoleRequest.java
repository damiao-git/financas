package com.house.financas.dto;

import com.house.financas.model.enums.RoleUsuario;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUsuarioRoleRequest {

    @NotNull(message = "Perfil é obrigatório")
    private RoleUsuario role;
}
