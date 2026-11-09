package com.ottoluis.MaosAmigas.ia;

import com.ottoluis.MaosAmigas.models.Consulta;
import com.ottoluis.MaosAmigas.enums.StatusConsulta;
import com.ottoluis.MaosAmigas.models.SuportePsicologico;
import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.repository.ConsultaRepository;
import com.ottoluis.MaosAmigas.repository.SuportePsicologicoRepository;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class IaToolService {

    private final SuportePsicologicoRepository psicologoRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;

    public IaToolService(
            SuportePsicologicoRepository psicologoRepository,
            ConsultaRepository consultaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.psicologoRepository = psicologoRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /*
     * ============================================================
     * BUSCAR PSICÓLOGOS
     * ============================================================
     */

    public List<SuportePsicologico> buscarPsicologos() {

        return psicologoRepository.findAll();
    }

    /*
     * ============================================================
     * VERIFICAR DISPONIBILIDADE
     * ============================================================
     */


    public List<LocalTime> listarHorariosDisponiveis(
            Long psicologoId, LocalDate data) {

        List<LocalTime> horarios = new java.util.ArrayList<>();

        if (psicologoId == null || data == null
                || data.isBefore(LocalDate.now())
                || data.getDayOfWeek().getValue() >= 6) {
            return horarios;
        }

        if (!psicologoRepository.existsById(psicologoId)) {
            return horarios;
        }

        for (LocalTime hora = LocalTime.of(8, 0);
             !hora.plusMinutes(50).isAfter(LocalTime.of(18, 0));
             hora = hora.plusMinutes(50)) {

            if (data.equals(LocalDate.now())
                    && !hora.isAfter(LocalTime.now())) {
                continue;
            }

            boolean ocupado = consultaRepository.existeConflitoHorario(
                    psicologoId,
                    data,
                    hora.minusMinutes(50),
                    hora.plusMinutes(50),
                    StatusConsulta.CANCELADA
            );

            if (!ocupado) {
                horarios.add(hora);
            }
        }

        return horarios;
    }

    public boolean verificarDisponibilidade(
            Long psicologoId, LocalDate data, LocalTime hora) {
        return hora != null
                && listarHorariosDisponiveis(psicologoId, data)
                        .contains(hora);
    }


    /*
     * ============================================================
     * CRIAR CONSULTA
     * ============================================================
     */

    public Consulta criarConsulta(
            Long psicologoId,
            LocalDate data,
            LocalTime hora,
            String observacao
    ) {

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "INICIANDO CRIAÇÃO DE CONSULTA PELA IA"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Psicólogo ID: "
                        + psicologoId
        );

        System.out.println(
                "Data: "
                        + data
        );

        System.out.println(
                "Hora: "
                        + hora
        );

        System.out.println(
                "Observação: "
                        + observacao
        );


        /*
         * ============================================================
         * 1. RECUPERA AUTENTICAÇÃO
         * ============================================================
         */

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "AUTENTICAÇÃO"
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Authentication: "
                        + authentication
        );


        /*
         * Verifica se existe autenticação.
         */

        if (authentication == null) {

            System.out.println(
                    "ERRO: Authentication está NULL."
            );

            throw new RuntimeException(
                    "Usuário não autenticado. "
                            + "O SecurityContext não possui autenticação."
            );
        }


        /*
         * Mostra o tipo de autenticação.
         */

        System.out.println(
                "Tipo de Authentication: "
                        + authentication
                        .getClass()
                        .getSimpleName()
        );


        /*
         * Mostra o nome do usuário.
         */

        System.out.println(
                "Nome autenticado: "
                        + authentication.getName()
        );


        /*
         * Mostra se está autenticado.
         */

        System.out.println(
                "Autenticado: "
                        + authentication.isAuthenticated()
        );


        /*
         * Mostra as roles.
         */

        System.out.println(
                "Authorities: "
                        + authentication.getAuthorities()
        );


        /*
         * ============================================================
         * 2. VALIDA AUTENTICAÇÃO
         * ============================================================
         */

        if (!authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "O usuário não está autenticado."
            );
        }


        /*
         * ============================================================
         * 3. RECUPERA E-MAIL
         * ============================================================
         */

        String email =
                authentication.getName();

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "E-mail identificado: "
                        + email
        );

        System.out.println(
                "------------------------------------------"
        );


        if (email == null || email.isBlank()) {

            throw new RuntimeException(
                    "Não foi possível obter o e-mail "
                            + "do usuário autenticado."
            );
        }


        /*
         * ============================================================
         * 4. BUSCA USUÁRIO NO BANCO
         * ============================================================
         */

        Usuario usuario =
                usuarioRepository.findByEmail(email);


        if (usuario == null) {

            System.out.println(
                    "ERRO: Usuário não encontrado."
            );

            System.out.println(
                    "E-mail pesquisado: "
                            + email
            );

            throw new RuntimeException(
                    "Usuário autenticado não encontrado "
                            + "no banco de dados pelo e-mail: "
                            + email
            );
        }


        System.out.println(
                "Usuário encontrado com sucesso."
        );

        System.out.println(
                "ID do usuário: "
                        + usuario.getId()
        );

        System.out.println(
                "Nome do usuário: "
                        + usuario.getNome()
        );

        System.out.println(
                "E-mail do usuário: "
                        + usuario.getEmail()
        );


        /*
         * ============================================================
         * 5. BUSCA PSICÓLOGO
         * ============================================================
         */

        SuportePsicologico psicologo =
                psicologoRepository
                        .findById(psicologoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Psicólogo não encontrado. "
                                                + "ID: "
                                                + psicologoId
                                )
                        );


        System.out.println(
                "Psicólogo encontrado: "
                        + psicologo.getNomePsicologa()
        );


        /*
         * ============================================================
         * 6. VERIFICA DISPONIBILIDADE NOVAMENTE
         * ============================================================
         */

        boolean disponivel =
                verificarDisponibilidade(
                        psicologoId,
                        data,
                        hora
                );


        System.out.println(
                "Horário disponível: "
                        + disponivel
        );


        if (!disponivel) {

            throw new RuntimeException(
                    "O horário informado já está ocupado."
            );
        }


        /*
         * ============================================================
         * 7. CRIA CONSULTA
         * ============================================================
         */

        Consulta consulta =
                new Consulta();


        consulta.setUsuario(
                usuario
        );


        consulta.setPsicologo(
                psicologo
        );


        consulta.setData(
                data
        );


        consulta.setHora(
                hora
        );


        consulta.setObservacao(
                observacao
        );


        /*
         * ============================================================
         * 8. SALVA NO BANCO
         * ============================================================
         */

        Consulta consultaSalva;

        try {

            consultaSalva =
                    consultaRepository.save(
                            consulta
                    );

        } catch (Exception e) {

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "ERRO AO SALVAR CONSULTA"
            );

            System.out.println(
                    "=========================================="
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Erro ao salvar a consulta no banco de dados: "
                            + e.getMessage(),
                    e
            );
        }


        /*
         * ============================================================
         * 9. SUCESSO
         * ============================================================
         */

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "CONSULTA CRIADA COM SUCESSO"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "ID da consulta: "
                        + consultaSalva.getId()
        );

        System.out.println(
                "Usuário: "
                        + usuario.getNome()
        );

        System.out.println(
                "Usuário ID: "
                        + usuario.getId()
        );

        System.out.println(
                "Psicólogo: "
                        + psicologo.getNomePsicologa()
        );

        System.out.println(
                "Psicólogo ID: "
                        + psicologo.getId()
        );

        System.out.println(
                "Data: "
                        + consultaSalva.getData()
        );

        System.out.println(
                "Hora: "
                        + consultaSalva.getHora()
        );

        System.out.println(
                "Status: "
                        + consultaSalva.getStatus()
        );

        System.out.println(
                "=========================================="
        );


        return consultaSalva;
    }
}
