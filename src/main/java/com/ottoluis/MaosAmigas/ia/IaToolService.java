
package com.ottoluis.MaosAmigas.ia;

import com.ottoluis.MaosAmigas.dto.ConsultaResponseDTO;
import com.ottoluis.MaosAmigas.dto.createDTO.ConsultaCreateDTO;
import com.ottoluis.MaosAmigas.enums.StatusConsulta;
import com.ottoluis.MaosAmigas.models.SuportePsicologico;
import com.ottoluis.MaosAmigas.repository.ConsultaRepository;
import com.ottoluis.MaosAmigas.repository.SuportePsicologicoRepository;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;
import com.ottoluis.MaosAmigas.services.ConsultaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IaToolService {

    private final SuportePsicologicoRepository psicologoRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaService consultaService;

    public IaToolService(
            SuportePsicologicoRepository psicologoRepository,
            ConsultaRepository consultaRepository,
            UsuarioRepository usuarioRepository,
            ConsultaService consultaService
    ) {
        this.psicologoRepository = psicologoRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
        this.consultaService = consultaService;
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
            Long psicologoId,
            LocalDate data
    ) {
        List<LocalTime> horarios = new ArrayList<>();

        if (psicologoId == null
                || data == null
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
            Long psicologoId,
            LocalDate data,
            LocalTime hora
    ) {
        return hora != null
                && listarHorariosDisponiveis(psicologoId, data)
                .contains(hora);
    }

    /*
     * ============================================================
     * CRIAR CONSULTA
     * ============================================================
     *
     * A IA não salva mais consultas diretamente no repository.
     * A criação é delegada ao ConsultaService, que centraliza
     * as validações e o controle de conflitos de horário.
     *
     */

    public ConsultaResponseDTO criarConsulta(
            Long psicologoId,
            LocalDate data,
            LocalTime hora,
            String observacao
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new RuntimeException(
                    "Usuário não autenticado."
            );
        }

        String email = authentication.getName();

        ConsultaCreateDTO dados = new ConsultaCreateDTO(
                psicologoId,
                data,
                hora,
                observacao
        );

        return consultaService.salvar(
                dados,
                email
        );
    }
}
