package com.paulo.backend.service;

import com.paulo.backend.dtos.FindUsuarioRequest;
import com.paulo.backend.dtos.UsuarioRequest;
import com.paulo.backend.dtos.UsuarioResponse;
import com.paulo.backend.entity.EnumAcesso;
import com.paulo.backend.entity.UsuarioEntity;
import com.paulo.backend.exception.UsuarioJaCadastradoException;
import com.paulo.backend.exception.UsuarioNaoCadastradoException;
import com.paulo.backend.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository
                .findByUsuario(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getSenhaHash())
                .authorities(usuario.getAcesso().name())
                .build();
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

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodosOsUsuarios(){
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public UsuarioResponse listarUsuarioEspecifico(FindUsuarioRequest usuario){

        UsuarioEntity user = usuarioRepository.findByUsuario(usuario.usuario())
                .orElseThrow(() ->
                        new UsuarioNaoCadastradoException(usuario.usuario())
                );

        return toResponse(user);

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
