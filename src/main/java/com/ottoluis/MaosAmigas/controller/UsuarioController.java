package com.ottoluis.MaosAmigas.controller;


import com.ottoluis.MaosAmigas.dto.createDTO.CreateUsuarioDTO;
import com.ottoluis.MaosAmigas.dto.UsuarioDTO;
import com.ottoluis.MaosAmigas.exception.RecursoNaoEncontradoException;
import com.ottoluis.MaosAmigas.mapper.UsuarioMapper;
import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;
import com.ottoluis.MaosAmigas.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuários não encontrado"
            )
    })
    @Operation(
            summary = "Buscar usuário",
            description = "Realiza Busca de todos usuários."
    )

    @GetMapping
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toDto)
                .toList();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado pelo /{id} informado"
            )
    })
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Realiza busca do usuário por ID."
    )

    @GetMapping("/{id}") //Buscar por id
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id){
        UsuarioDTO dto = usuarioService.buscarPorId(id);

        return ResponseEntity.ok(dto);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado"
            )
    })

    @Operation(
            summary = "Cadastrar usuário",
            description = "Realiza o cadastro de um novo usuário no sistema."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public UsuarioDTO salvar(@Valid @RequestBody CreateUsuarioDTO dto) {

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioMapper.toDto(usuarioSalvo);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public UsuarioDTO atualizar(
            @PathVariable Long id,
            @RequestBody CreateUsuarioDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCpf(dto.getCpf());
        usuario.setEndereco(dto.getEndereco());
        usuario.setContatoConfianca(dto.getContatoConfianca());
        usuario.setContatoPessoal(dto.getContatoPessoal());
        usuario.setDataNascimento(dto.getDataNascimento());

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return UsuarioMapper.toDto(usuarioAtualizado);

    }
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado pelo /{id} informado"
            )
    })
    @Operation(
            summary = "Deletar usuário por ID",
            description = "Deletar o usuário por ID."
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuario", id);
        }
        usuarioRepository.deleteById(id);
    }
}