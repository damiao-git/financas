package com.house.financas.repository;

import com.house.financas.model.GoogleCalendarIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoogleCalendarIntegrationRepository extends JpaRepository<GoogleCalendarIntegration, Long> {

    Optional<GoogleCalendarIntegration> findByUsuarioId(Long usuarioId);

    Optional<GoogleCalendarIntegration> findByAuthState(String authState);

    List<GoogleCalendarIntegration> findAllByGoogleAccountEmailIgnoreCaseAndConectadoTrue(String googleAccountEmail);
}
