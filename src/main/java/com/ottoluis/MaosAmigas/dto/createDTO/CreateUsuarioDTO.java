package com.ottoluis.MaosAmigas.dto.createDTO;

import com.ottoluis.MaosAmigas.models.embeddable.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CreateUsuarioDTO {

    private Long id;

    @NotNull(message = "Endereço obrigatório")
    @Valid
    private Endereco endereco;
@NotBlank(message = "Nome obrigatorio")
    private String nome;


    @NotBlank(message = "Email obrigatorio")
    @Email(message = "Email inválido")
    private String email;


    @Size(min = 6)
    @NotBlank(message = "Senha obrigatorio")
    private String senha;

    @NotBlank(message = "Data de nascimento obrigatória")
    private String dataNascimento;

    @NotBlank(message = "Contato Pessoal obrigatorio")
    private String contatoPessoal;

    private String contatoConfianca;

    public CreateUsuarioDTO() {}

    public CreateUsuarioDTO(Long id, String nome, String email, String senha, String dataNascimento, Endereco endereco, String contatoPessoal, String contatoConfianca) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.contatoPessoal = contatoPessoal;
        this.contatoConfianca = contatoConfianca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getContatoPessoal() {
        return contatoPessoal;
    }

    public void setContatoPessoal(String contatoPessoal) {
        this.contatoPessoal = contatoPessoal;
    }

    public String getContatoConfianca() {
        return contatoConfianca;
    }

    public void setContatoConfianca(String contatoConfianca) {
        this.contatoConfianca = contatoConfianca;
    }


}

