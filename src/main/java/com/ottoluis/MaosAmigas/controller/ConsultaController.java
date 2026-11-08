
package com.ottoluis.MaosAmigas.controller;

import com.ottoluis.MaosAmigas.dto.ConsultaResponseDTO;
import com.ottoluis.MaosAmigas.dto.createDTO.ConsultaCreateDTO;
import com.ottoluis.MaosAmigas.services.ConsultaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USUARIO')")
    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listar(
            Authentication authentication
    ) {
        boolean administrador = ehAdministrador(authentication);

        return ResponseEntity.ok(
                consultaService.listar(authentication.getName(), administrador)
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USUARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                consultaService.buscarPorId(
                        id,
                        authentication.getName(),
                        ehAdministrador(authentication)
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USUARIO')")
    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> salvar(
            @Valid @RequestBody ConsultaCreateDTO dados,
            Authentication authentication
    ) {
        ConsultaResponseDTO consulta =
                consultaService.salvar(dados, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USUARIO')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponseDTO> cancelar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ConsultaResponseDTO consulta = consultaService.cancelar(
                id,
                authentication.getName(),
                ehAdministrador(authentication)
        );

        return ResponseEntity.ok(consulta);
    }

    private boolean ehAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
