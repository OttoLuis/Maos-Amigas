package com.ottoluis.MaosAmigas;

import com.ottoluis.MaosAmigas.repository.SuportePsicologicoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MaosAmigasApplicationTests {

    @Autowired
    private SuportePsicologicoRepository psicologoRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void deveBuscarPsicologoComBloqueioPessimista() {
        var psicologo = psicologoRepository.buscarPorIdComBloqueio(1L);

        assertTrue(
                psicologo.isPresent(),
                "O psicólogo de ID 1 deveria existir."
        );
    }
}
