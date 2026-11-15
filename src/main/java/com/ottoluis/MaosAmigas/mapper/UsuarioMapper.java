package com.ottoluis.MaosAmigas.mapper;

import com.ottoluis.MaosAmigas.dto.createDTO.CreateUsuarioDTO;
import com.ottoluis.MaosAmigas.dto.UsuarioDTO;
import com.ottoluis.MaosAmigas.models.Usuario;

public class UsuarioMapper {

    //conversao entidade para DTO
    public static UsuarioDTO toDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setContatoPessoal(usuario.getContatoPessoal());
        dto.setContatoConfianca(usuario.getContatoConfianca());
        dto.setEndereco(usuario.getEndereco());


        return dto;
    }

    //conversao de DTO para ENTIDADE
    public static Usuario toEntity(CreateUsuarioDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setId(dto.getId());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setContatoPessoal(dto.getContatoPessoal());
        usuario.setContatoConfianca(dto.getContatoConfianca());
        usuario.setEndereco(dto.getEndereco());


        return usuario;


    }
}
