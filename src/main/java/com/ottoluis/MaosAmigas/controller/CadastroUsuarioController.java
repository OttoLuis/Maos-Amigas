package com.ottoluis.MaosAmigas.controller;

import com.ottoluis.MaosAmigas.dto.UsuarioDTO;
import com.ottoluis.MaosAmigas.dto.createDTO.CreateUsuarioDTO;
import com.ottoluis.MaosAmigas.mapper.UsuarioMapper;
import com.ottoluis.MaosAmigas.models.Role;
import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.repository.RoleRepository;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class CadastroUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioController(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> cadastrar(
            @Valid @RequestBody CreateUsuarioDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }
Role roleUsuario = roleRepository
                .findByAuthority("ROLE_USUARIO")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "A permissão de usuário não foi encontrada."
                ));

        Usuario usuario = UsuarioMapper.toEntity(dto);

        // O ID é gerado pelo banco.
        usuario.setId(null);

        // A senha nunca deve ser armazenada em texto puro.
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        //Todo cadastro público recebe somente a permissão de usuário.
        usuario.addRole(roleUsuario);

        Usuario salvo = usuarioRepository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UsuarioMapper.toDto(salvo));
    }
}
