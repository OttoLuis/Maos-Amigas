
package com.ottoluis.MaosAmigas.services;

import com.ottoluis.MaosAmigas.dto.ConsultaResponseDTO;
import com.ottoluis.MaosAmigas.dto.createDTO.ConsultaCreateDTO;
import com.ottoluis.MaosAmigas.enums.StatusConsulta;
import com.ottoluis.MaosAmigas.models.Consulta;
import com.ottoluis.MaosAmigas.models.SuportePsicologico;
import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.repository.ConsultaRepository;
import com.ottoluis.MaosAmigas.repository.SuportePsicologicoRepository;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConsultaService {

    private static final LocalTime INICIO_EXPEDIENTE = LocalTime.of(8, 0);
    private static final LocalTime FIM_EXPEDIENTE = LocalTime.of(18, 0);
    private static final int DURACAO_CONSULTA_MINUTOS = 50;

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SuportePsicologicoRepository psicologoRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            UsuarioRepository usuarioRepository,
            SuportePsicologicoRepository psicologoRepository
    ) {
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
        this.psicologoRepository = psicologoRepository;
    }

    public List<ConsultaResponseDTO> listar(
            String email,
            boolean administrador
    ) {
        List<Consulta> consultas = administrador
                ? consultaRepository.findAll()
                : consultaRepository.findByUsuario_EmailOrderByDataAscHoraAsc(email);

        return consultas.stream()
                .map(this::paraResposta)
                .toList();
    }

    public ConsultaResponseDTO buscarPorId(
            Long id,
            String email,
            boolean administrador
    ) {
        Consulta consulta;

        if (administrador) {
            consulta = consultaRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Consulta não encontrada."
                    ));
        } else {
            consulta = consultaRepository.findByIdAndUsuario_Email(id, email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Consulta não encontrada."
                    ));
        }

        return paraResposta(consulta);
    }

    @Transactional
    public ConsultaResponseDTO salvar(
            ConsultaCreateDTO dados,
            String email
    ) {
        if (dados.getData() == null
                || dados.getHora() == null
                || dados.getPsicologoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o psicólogo, a data e o horário."
            );
        }

        LocalDate data = dados.getData();
        LocalTime hora = dados.getHora();
        LocalTime horaFim = hora.plusMinutes(DURACAO_CONSULTA_MINUTOS);

        if (data.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data da consulta não pode estar no passado."
            );
        }

        if (data.isEqual(LocalDate.now()) && !hora.isAfter(LocalTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O horário da consulta não pode estar no passado."
            );
        }

        DayOfWeek diaSemana = data.getDayOfWeek();

        if (diaSemana == DayOfWeek.SATURDAY
                || diaSemana == DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "As consultas são realizadas somente de segunda a sexta."
            );
        }

        if (hora.isBefore(INICIO_EXPEDIENTE)
                || horaFim.isAfter(FIM_EXPEDIENTE)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O horário deve estar entre 08h e 18h, respeitando os 50 minutos de consulta."
            );
        }

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        SuportePsicologico psicologo = psicologoRepository
                .findById(dados.getPsicologoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Psicólogo não encontrado."
                ));

        LocalTime horaLimiteInferior =
                hora.minusMinutes(DURACAO_CONSULTA_MINUTOS);

        boolean horarioOcupado = consultaRepository.existeConflitoHorario(
                psicologo.getId(),
                data,
                horaLimiteInferior,
                horaFim,
                StatusConsulta.CANCELADA
        );

        if (horarioOcupado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este horário conflita com outra consulta. Escolha outro."
            );
        }

        Consulta consulta = new Consulta();
        consulta.setUsuario(usuario);
        consulta.setPsicologo(psicologo);
        consulta.setData(data);
        consulta.setHora(hora);
        consulta.setObservacao(dados.getObservacao());
        consulta.setStatus(StatusConsulta.AGENDADA);

        return paraResposta(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponseDTO cancelar(
            Long id,
            String email,
            boolean administrador
    ) {
        Consulta consulta;

        if (administrador) {
            consulta = consultaRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Consulta não encontrada."
                    ));
        } else {
            consulta = consultaRepository.findByIdAndUsuario_Email(id, email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Consulta não encontrada."
                    ));
        }

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta consulta já está cancelada."
            );
        }

        if (consulta.getStatus() == StatusConsulta.CONCLUIDA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível cancelar uma consulta concluída."
            );
        }

        consulta.setStatus(StatusConsulta.CANCELADA);

        return paraResposta(consultaRepository.save(consulta));
    }

    private ConsultaResponseDTO paraResposta(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getPsicologo().getId(),
                consulta.getPsicologo().getNomePsicologa(),
                consulta.getData(),
                consulta.getHora(),
                consulta.getStatus(),
                consulta.getObservacao(),
                consulta.getLinkAtendimento()
        );
    }
}
