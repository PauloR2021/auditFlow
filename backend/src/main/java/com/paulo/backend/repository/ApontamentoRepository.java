package com.paulo.backend.repository;

import com.paulo.backend.entity.apontamento.ApontamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApontamentoRepository extends JpaRepository<ApontamentoEntity,Long> {

    List<ApontamentoEntity> findByIdResponsavel_Id(Long idResponsavel);
}
