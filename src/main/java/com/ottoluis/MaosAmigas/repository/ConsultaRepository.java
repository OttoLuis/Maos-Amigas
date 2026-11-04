
package com.ottoluis.MaosAmigas.repository;

import com.ottoluis.MaosAmigas.enums.StatusConsulta;
import com.ottoluis.MaosAmigas.models.Consulta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
        SELECT COUNT(c) > 0
        FROM Consulta c
        WHERE c.psicologo.id = :psicologoId
          AND c.data = :data
          AND c.status <> :statusCancelado
          AND c.hora > :horaLimiteInferior
          AND c.hora < :horaFim
        """)
    boolean existeConflitoHorario(
            @Param("psicologoId") Long psicologoId,
            @Param("data") LocalDate data,
            @Param("horaLimiteInferior") LocalTime horaLimiteInferior,
            @Param("horaFim") LocalTime horaFim,
            @Param("statusCancelado") StatusConsulta statusCancelado
    );

    List<Consulta> findByUsuario_EmailOrderByDataAscHoraAsc(String email);

    Optional<Consulta> findByIdAndUsuario_Email(Long id, String email);
}
