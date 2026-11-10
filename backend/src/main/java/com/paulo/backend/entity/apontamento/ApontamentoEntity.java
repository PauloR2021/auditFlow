package com.paulo.backend.entity.apontamento;

import com.paulo.backend.entity.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="apontamentos_tb")
@Getter @Setter
public class ApontamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_responsavel", nullable = true)
    private UsuarioEntity idResponsavel;

    private String responsavel;

    @Column(name = "prazo", nullable = false)
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    @Column(name ="situacao",nullable = false)
    private EnumSituacao situacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_criador", nullable = false)
    private UsuarioEntity idCriador;


    private String usuarioCriador;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @PrePersist
    private void antesDeSalvar() {
        this.dtCriacao = LocalDateTime.now();
    }

}
