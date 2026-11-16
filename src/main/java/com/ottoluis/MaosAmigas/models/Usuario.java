
package com.ottoluis.MaosAmigas.models;

import io.swagger.v3.oas.annotations.media.Schema;
import com.ottoluis.MaosAmigas.models.embeddable.Endereco;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "usuario_tb")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Endereco endereco;

    @Schema(
            description = "Nome completo do usuário",
            example = "Joao da Silva Santos"
    )
    @Column(name = "nome", nullable = false)
    @NotBlank(message = "esse campo não pode ser vazio")
    private String nome;

    @Schema(
            description = "Email do usário",
            example = "joao@gmail.com"
    )
    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "esse campo não pode ser vazio")
    private String email;

    @Schema(
            description = "Senha do Usuário(Min 6 caracteres)",
            example = "12@456"
    )
    @Column(name = "senha", nullable = false)
    @Size(min = 6, message = "esse campo precisa ter no minimo 6 caracteres")
    @NotBlank(message = "esse campo não pode ser vazio")
    private String senha;

    @Schema(
            description = "Data de nascimento do usuário",
            example = "10/10/2010"
    )
    @Column(name = "data_nascimento", nullable = true)
    private String dataNascimento;

    @Schema(
            description = "Contato do usuário",
            example = "(99)99999-9999"
    )
    @Column(name = "contato_pessoal", nullable = true)
    private String contatoPessoal;

    @Schema(
            description = "Contato de confiança do usário",
            example = "(99)99999-9999"
    )
    @Column(name = "contato_confianca", nullable = true)
    private String contatoConfianca;

    @ManyToMany
    @JoinTable(
            name = "usuario_role_tb",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha,
                   String dataNascimento, Endereco endereco,
                   String contatoPessoal, String contatoConfianca) {
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

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean hasRole(String roleNome) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleNome)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public @Nullable String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
