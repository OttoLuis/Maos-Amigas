package com.ottoluis.MaosAmigas.controller;

import com.ottoluis.MaosAmigas.ia.IaToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/ia/ferramentas")
public class IaFerramentasController {

    private final IaToolService iaToolService;

    public IaFerramentasController(IaToolService iaToolService) {
        this.iaToolService = iaToolService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USUARIO')")
    @GetMapping("/horarios")
    public ResponseEntity<List<LocalTime>> listarHorariosDisponiveis(
            @RequestParam Long psicologoId,
            @RequestParam LocalDate data
    ) {
        List<LocalTime> horarios =
                iaToolService.listarHorariosDisponiveis(
                        psicologoId,
                        data
                );

        return ResponseEntity.ok(horarios);
    }
}
