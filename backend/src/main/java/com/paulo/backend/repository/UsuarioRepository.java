package com.paulo.backend.repository;

import com.paulo.backend.entity.usuario.EnumAcesso;
import com.paulo.backend.entity.usuario.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {


    Optional<UsuarioEntity> findByUsuario(String usuario);
    Optional<UsuarioEntity> findByIdAndAcesso(Long id, EnumAcesso acesso);

    boolean existsByUsuario(String usuario);



}
