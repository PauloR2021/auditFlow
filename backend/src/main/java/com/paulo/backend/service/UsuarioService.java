package com.paulo.backend.service;

import com.paulo.backend.dtos.FindUsuarioRequest;
import com.paulo.backend.dtos.TrocarSenhaRequest;
import com.paulo.backend.dtos.UsuarioRequest;
import com.paulo.backend.dtos.UsuarioResponse;
import com.paulo.backend.entity.EnumAcesso;
import com.paulo.backend.entity.UsuarioEntity;
import com.paulo.backend.exception.UsuarioJaCadastradoException;
import com.paulo.backend.exception.UsuarioNaoCadastradoException;
import com.paulo.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        novoUsuario.setSenhaHash(passwordEncoder.encode("123456789"));
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
        novoUsuario.setSenhaHash(passwordEncoder.encode("123456789"));
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
    public UsuarioResponse listarUsuarioEspecifico(String usuario){

        UsuarioEntity user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() ->
                        new UsuarioNaoCadastradoException(usuario)
                );

        return toResponse(user);

    }

    @Transactional
    public void trocarSenhaPrimeiroAcesso(
            String usuario,
            TrocarSenhaRequest request
    ) {
        UsuarioEntity user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow( () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        if (!Boolean.TRUE.equals(user.getPrimeiroAcesso())
                || !Boolean.FALSE.equals(user.getTrocouSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A troca de senha de primeiro acesso não está pendente"
            );
        }
        if (!request.senha().equals(request.confirmarSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "As senhas não coincidem"
            );
        }

        if (passwordEncoder.matches(
                request.senha(),
                user.getSenhaHash()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A nova senha deve ser diferente da senha atual"
            );
        }

        user.setSenhaHash(passwordEncoder.encode(request.senha()));
        user.setPrimeiroAcesso(false);
        user.setTrocouSenha(true);

        usuarioRepository.save(user);
    }



    private UsuarioResponse toResponse(UsuarioEntity usuario){
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getDtCriacao(),
                usuario.getAcesso().toString(),
                usuario.getPrimeiroAcesso(),
                usuario.getTrocouSenha()
        );
    }



}
