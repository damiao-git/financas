package com.house.financas.service;

public interface EmailService {

    void enviarResetSenha(String destinatario, String nome, String resetLink);
}
