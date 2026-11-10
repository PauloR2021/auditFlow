package com.paulo.backend.service;

import com.paulo.backend.dtos.Apontamento.ApontamentoRequest;
import com.paulo.backend.dtos.Apontamento.ApontamentoResponse;
import com.paulo.backend.entity.apontamento.ApontamentoEntity;
import com.paulo.backend.entity.apontamento.EnumSituacao;
import com.paulo.backend.entity.usuario.EnumAcesso;
import com.paulo.backend.entity.usuario.UsuarioEntity;
import com.paulo.backend.exception.ApontamentoFinalizado;
import com.paulo.backend.exception.ApontamentoNãoCadastrado;
import com.paulo.backend.exception.UsuarioNaoCadastradoException;
import com.paulo.backend.exception.UsuarioNaoTemAcessoAdmin;
import com.paulo.backend.repository.ApontamentoRepository;
import com.paulo.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ApontamentoService {

    private final UsuarioRepository usuarioRepository;
    private final ApontamentoRepository apontamentoRepository;

    public ApontamentoService(
            UsuarioRepository usuarioRepository,
            ApontamentoRepository apontamentoRepository
    ){
        this.usuarioRepository = usuarioRepository;
        this.apontamentoRepository = apontamentoRepository;
    }

    @Transactional
    public ApontamentoResponse create(ApontamentoRequest request){

        UsuarioEntity userResponsavel = usuarioRepository.findByUsuario(request.responsavel())
                .orElseThrow(() ->
                        new UsuarioNaoCadastradoException("Usuário Comum: " +request.responsavel())
                );

        UsuarioEntity userAdmin = usuarioRepository.findByIdAndAcesso(request.idCriador(), EnumAcesso.ADMIN)
                .orElseThrow(() ->
                        new UsuarioNaoTemAcessoAdmin(request.idCriador().toString())
                );

        ApontamentoEntity apontamento = new ApontamentoEntity();
        apontamento.setTitulo(request.titulo());
        apontamento.setDescricao(request.descricao());
        apontamento.setIdResponsavel(userResponsavel);
        apontamento.setResponsavel(userResponsavel.getNome());
        apontamento.setPrazo(request.prazo());
        apontamento.setSituacao(EnumSituacao.ABERTO);
        apontamento.setIdCriador(userAdmin);
        apontamento.setUsuarioCriador(userAdmin.getNome());

        ApontamentoEntity novoApontamento = apontamentoRepository.save(apontamento);

        return toResponse(novoApontamento);

    }

    @Transactional(readOnly = true)
    public List<ApontamentoResponse> listarTodosApontamentos(){
        return apontamentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApontamentoResponse> listarApontamentosIdResponsavel(Long idResponsavel) {
        return apontamentoRepository.findByIdResponsavel_Id(idResponsavel)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ApontamentoResponse mudarSituacaoParaConculuido(Long id){
        ApontamentoEntity apontamento = apontamentoRepository.findById(id)
                .orElseThrow(() -> new ApontamentoNãoCadastrado(id));

        if(apontamento.getSituacao() == EnumSituacao.FINALIZADO){
            throw new ApontamentoFinalizado(apontamento.getTitulo());
        }

        apontamento.setSituacao(EnumSituacao.CONCLUIDO);


        return toResponse(apontamento);
    }

    @Transactional
    public ApontamentoResponse mudarSituacaoParaPendente(Long id){
        ApontamentoEntity apontamento = apontamentoRepository.findById(id)
                .orElseThrow(() -> new ApontamentoNãoCadastrado(id));

        if(apontamento.getSituacao() == EnumSituacao.FINALIZADO){
            throw new ApontamentoFinalizado(apontamento.getTitulo());
        }

        apontamento.setSituacao(EnumSituacao.PENDENTE);

        return toResponse(apontamento);
    }

    @Transactional
    public ApontamentoResponse mudarSituacaoParaFinalizado(Long id){
        ApontamentoEntity apontamento = apontamentoRepository.findById(id)
                .orElseThrow(() -> new ApontamentoNãoCadastrado(id));

        if(apontamento.getSituacao() == EnumSituacao.FINALIZADO){
            throw new ApontamentoFinalizado(apontamento.getTitulo());
        }

        apontamento.setSituacao(EnumSituacao.FINALIZADO);

        return toResponse(apontamento);
    }

    @Transactional
    public ApontamentoResponse deletarApontamento(Long id){
        ApontamentoEntity apontamento = apontamentoRepository.findById(id)
                .orElseThrow( () -> new ApontamentoNãoCadastrado(id));

        apontamentoRepository.delete(apontamento);

        return toResponse(apontamento);
    }

    @Transactional
    public ApontamentoResponse removerResponsavelDoApontamento(Long id){
        ApontamentoEntity apontamento = apontamentoRepository.findById(id)
                .orElseThrow( () -> new ApontamentoNãoCadastrado(id));

        if (apontamento.getSituacao() == EnumSituacao.CONCLUIDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível remover o responsável de um apontamento concluído."
            );
        }

        apontamento.setIdResponsavel(null);
        apontamento.setResponsavel(null);

        return toResponse(apontamento);
    }



    private ApontamentoResponse toResponse(ApontamentoEntity apontamentoEntity){
        return new ApontamentoResponse(
                apontamentoEntity.getId(),
                apontamentoEntity.getTitulo(),
                apontamentoEntity.getDescricao(),
                apontamentoEntity.getIdResponsavel() == null
                        ? null
                        : apontamentoEntity.getIdResponsavel().getId(),
                apontamentoEntity.getResponsavel(),
                apontamentoEntity.getPrazo(),
                apontamentoEntity.getSituacao().toString(),
                apontamentoEntity.getIdCriador().getId(),
                apontamentoEntity.getUsuarioCriador(),
                apontamentoEntity.getDtCriacao()
        );
    }
}
