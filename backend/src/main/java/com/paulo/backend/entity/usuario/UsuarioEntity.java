package com.paulo.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_tb")
@Getter @Setter
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "usuario", length = 20, nullable = false)
    private String usuario;

    @Column(name = "senha_hash", length = 255, nullable = false)
    private String senhaHash;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    private void antesDeSalvar() {
        this.dtCriacao = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "acesso", length = 30, nullable = false)
    private EnumAcesso acesso;

    @Column(name = "primeiro_acesso", nullable = false)
    private Boolean primeiroAcesso = true;

    @Column(name = "trocou_senha", nullable = false)
    private Boolean trocouSenha = false;




}
