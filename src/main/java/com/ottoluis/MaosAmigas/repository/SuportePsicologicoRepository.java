package com.ottoluis.MaosAmigas.repository;

import com.ottoluis.MaosAmigas.models.SuportePsicologico;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SuportePsicologicoRepository extends JpaRepository<SuportePsicologico, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM SuportePsicologico p WHERE p.id = :id")
    Optional<SuportePsicologico> buscarPorIdComBloqueio(@Param("id") Long id);
}
