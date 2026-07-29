package com.house.financas.repository;

import com.house.financas.model.PasswordResetToken;
import com.house.financas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsedAtIsNull(String tokenHash);

    List<PasswordResetToken> findByUsuarioAndUsedAtIsNull(Usuario usuario);
}
