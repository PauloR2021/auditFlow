package com.paulo.backend.service;

import com.paulo.backend.dtos.UsuarioRequest;
import com.paulo.backend.dtos.UsuarioResponse;
import com.paulo.backend.entity.EnumAcesso;
import com.paulo.backend.entity.UsuarioEntity;
import com.paulo.backend.exception.UsuarioJaCadastradoException;
import com.paulo.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse criarUsuarioAdmin(UsuarioRequest request){

        if (usuarioRepository.existsByUsuario(request.usuario())){
            throw new UsuarioJaCadastradoException(request.usuario());
        }

        UsuarioEntity novoUsuario  = new UsuarioEntity();
        novoUsuario.setNome(request.nome());
        novoUsuario.setUsuario(request.usuario());
        novoUsuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        novoUsuario.setAcesso(EnumAcesso.ADMIN);

        UsuarioEntity usuarioSalvo = usuarioRepository.save(novoUsuario);
        return toResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse criarUsuarioUser(UsuarioRequest request){

        if (usuarioRepository.existsByUsuario(request.usuario())){
            throw new UsuarioJaCadastradoException(request.usuario());
        }

        UsuarioEntity novoUsuario  = new UsuarioEntity();
        novoUsuario.setNome(request.nome());
        novoUsuario.setUsuario(request.usuario());
        novoUsuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        novoUsuario.setAcesso(EnumAcesso.USER);

        UsuarioEntity usuarioSalvo = usuarioRepository.save(novoUsuario);
        return toResponse(usuarioSalvo);
    }



    private UsuarioResponse toResponse(UsuarioEntity usuario){
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getDtCriacao(),
                usuario.getAcesso().toString()
        );
    }


}
