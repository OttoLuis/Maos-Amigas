package com.ottoluis.MaosAmigas.repository;

import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.projection.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);
    boolean existsByEmail(String email);
    @Query(nativeQuery = true, value = """
          SELECT usuario_tb.email AS username, 
                 usuario_tb.senha AS password, 
                 role_tb.id AS roleId, 
                 role_tb.authority AS authority
          FROM usuario_tb
          INNER JOIN usuario_role_tb ON usuario_tb.id = usuario_role_tb.usuario_id
          INNER JOIN role_tb ON role_tb.id = usuario_role_tb.role_id
          WHERE usuario_tb.email = :email
       """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

}