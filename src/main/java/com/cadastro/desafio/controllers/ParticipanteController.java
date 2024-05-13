package com.cadastro.desafio.controllers;

import com.cadastro.desafio.dtos.ParticipanteDto;
import com.cadastro.desafio.entities.Participante;
import com.cadastro.desafio.entities.Usuario;
import com.cadastro.desafio.enums.EstadoCivilEnum;
import com.cadastro.desafio.enums.SexoEnum;
import com.cadastro.desafio.enums.StatusEnum;
import com.cadastro.desafio.response.Response;
import com.cadastro.desafio.services.ParticipanteService;
import com.cadastro.desafio.services.UsuarioService;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participantes")
@CrossOrigin(origins = "*")
public class ParticipanteController {

	private static final Logger log = LoggerFactory.getLogger(ParticipanteController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private ParticipanteService participanteService;

	@Autowired
	private UsuarioService usuarioService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public ParticipanteController() {
	}

	/**
	 * Retorna a listagem de participantes de um usuário.
	 * 
	 * @param usuarioId
	 * @return ResponseEntity<Response<ParticipanteDto>>
	 */
	@GetMapping(value = "/usuario/{usuarioId}")
	public ResponseEntity<Response<Page<ParticipanteDto>>> listarPorUsuarioId(
			@PathVariable("usuarioId") Long usuarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "data") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("Buscando participantes por ID do usuário: {}, página: {}", usuarioId, pag);
		Response<Page<ParticipanteDto>> response = new Response<Page<ParticipanteDto>>();

		PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<Participante> participantes = this.participanteService.buscarPorUsuarioId(usuarioId, pageRequest);
		Page<ParticipanteDto> participantesDto = participantes.map(participante -> this.converterParticipanteDto(participante));

		response.setData(participantesDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna a listagem de todos os participantes de um usuário.
	 *
	 * @param usuarioId
	 * @return ResponseEntity<Response<ParticipanteDto>>
	 */
	@GetMapping(value = "/usuario/{usuarioId}/todos")
	public ResponseEntity<Response<List<ParticipanteDto>>> listarTodosPorUsuarioId(
			@PathVariable("usuarioId") Long usuarioId) {
		log.info("Buscando todos os participantes por ID do usuário: {}", usuarioId);
		Response<List<ParticipanteDto>> response = new Response<List<ParticipanteDto>>();

		List<Participante> participantes = this.participanteService.buscarTodosPorUsuarioId(usuarioId);
		List<ParticipanteDto> participantesDto = participantes.stream()
				.map(participante -> this.converterParticipanteDto(participante))
				.collect(Collectors.toList());

		response.setData(participantesDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna um participante por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<ParticipanteDto>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<ParticipanteDto>> listarPorId(@PathVariable("id") Long id) {
		log.info("Buscando participante por ID: {}", id);
		Response<ParticipanteDto> response = new Response<ParticipanteDto>();
		Optional<Participante> participante = this.participanteService.buscarPorId(id);

		if (!participante.isPresent()) {
			log.info("Participante não encontrado para o ID: {}", id);
			response.getErrors().add("Participante não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterParticipanteDto(participante.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Adiciona um novo participante.
	 * 
	 * @param participanteDto
	 * @param result
	 * @return ResponseEntity<Response<ParticipanteDto>>
	 * @throws ParseException 
	 */
	@PostMapping
	public ResponseEntity<Response<ParticipanteDto>> adicionar(@Valid @RequestBody ParticipanteDto participanteDto,
			BindingResult result) throws ParseException {
		log.info("Adicionando participante: {}", participanteDto.toString());
		Response<ParticipanteDto> response = new Response<ParticipanteDto>();
		validarUsuario(participanteDto, result);
		Participante participante = this.converterDtoParaParticipante(participanteDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando participante: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		participante = this.participanteService.persistir(participante);
		response.setData(this.converterParticipanteDto(participante));
		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados de um participante.
	 * 
	 * @param id
	 * @param participanteDto
	 * @return ResponseEntity<Response<Participante>>
	 * @throws ParseException 
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<ParticipanteDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody ParticipanteDto participanteDto, BindingResult result) throws ParseException {
		log.info("Atualizando participante: {}", participanteDto.toString());
		Response<ParticipanteDto> response = new Response<ParticipanteDto>();
		validarUsuario(participanteDto, result);
		participanteDto.setId(Optional.of(id));
		Participante participante = this.converterDtoParaParticipante(participanteDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando participante: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		participante = this.participanteService.persistir(participante);
		response.setData(this.converterParticipanteDto(participante));
		return ResponseEntity.ok(response);
	}

	/**
	 * Remove um participante por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<Participante>>
	 */
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
		log.info("Removendo participante: {}", id);
		Response<String> response = new Response<String>();
		Optional<Participante> participante = this.participanteService.buscarPorId(id);

		if (!participante.isPresent()) {
			log.info("Erro ao remover devido ao participante ID: {} ser inválido.", id);
			response.getErrors().add("Erro ao remover participante. Registro não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.participanteService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}

	/**
	 * Retorna o último participante de um usuário.
	 *
	 * @param usuarioId
	 * @return ResponseEntity<Response<ParticipanteDto>>
	 */
	@GetMapping(value = "/usuario/{usuarioId}/ultimo")
	public ResponseEntity<Response<ParticipanteDto>> ultimoPorUsuarioId(
			@PathVariable("usuarioId") Long usuarioId) {
		log.info("Buscando o último participante por ID do usuário: {}", usuarioId);
		Response<ParticipanteDto> response = new Response<ParticipanteDto>();

		Optional<Participante> participante = this.participanteService.buscarUltimoPorUsuarioId(usuarioId);

		if (participante.isPresent()) {
			ParticipanteDto participanteDto = this.converterParticipanteDto(participante.get());
			response.setData(participanteDto);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Valida um usuário, verificando se ele é existente e válido no
	 * sistema.
	 * 
	 * @param participanteDto
	 * @param result
	 */
	private void validarUsuario(ParticipanteDto participanteDto, BindingResult result) {
		if (participanteDto.getUsuarioId() == null) {
			result.addError(new ObjectError("usuario", "Usuário não informado."));
			return;
		}

		log.info("Validando usuário id {}: ", participanteDto.getUsuarioId());
		Optional<Usuario> usuario = this.usuarioService.buscarPorId(participanteDto.getUsuarioId());
		if (!usuario.isPresent()) {
			result.addError(new ObjectError("usuario", "Usuário não encontrado. ID inexistente."));
		}
	}

	/**
	 * Converte uma entidade participante para seu respectivo DTO.
	 * 
	 * @param participante
	 * @return ParticipanteDto
	 */
	private ParticipanteDto converterParticipanteDto(Participante participante) {
		ParticipanteDto participanteDto = new ParticipanteDto();
		participanteDto.setId(Optional.of(participante.getId()));
		participanteDto.setUsuarioId(participante.getUsuario().getId());
		participanteDto.setCodigo(participante.getCodigo());
		participanteDto.setCodigoExterno(participante.getCodigoExterno());
		participanteDto.setNome(participante.getNome());
		participanteDto.setEmail(participante.getEmail());
		participanteDto.setCpf(participante.getCpf());
		participanteDto.setSexo(participante.getSexo().toString());
		participanteDto.setEstadoCivil(participante.getEstadoCivil().toString());
		participanteDto.setObservacao(participante.getObservacao());
		participanteDto.setCodigo(participante.getCodigo());
		participanteDto.setData(this.dateFormat.format(participante.getData()));
		participanteDto.setStatus(participante.getStatus().toString());

		return participanteDto;
	}

	/**
	 * Converte um ParticipanteDto para uma entidade Participante.
	 * 
	 * @param participanteDto
	 * @param result
	 * @return Participante
	 * @throws ParseException 
	 */
	private Participante converterDtoParaParticipante(ParticipanteDto participanteDto, BindingResult result) throws ParseException {
		Participante participante = new Participante();

		if (participanteDto.getId().isPresent()) {
			Optional<Participante> lanc = this.participanteService.buscarPorId(participanteDto.getId().get());
			if (lanc.isPresent()) {
				participante = lanc.get();
			} else {
				result.addError(new ObjectError("participante", "Participante não encontrado."));
			}
		} else {
			participante.setUsuario(new Usuario());
			participante.getUsuario().setId(participanteDto.getUsuarioId());
		}

		participante.setCodigo(participanteDto.getCodigo());

		participante.setCodigoExterno(participanteDto.getCodigoExterno());

		participante.setNome(participanteDto.getNome());

		participante.setEmail(participanteDto.getEmail());

		participante.setCpf(participanteDto.getCpf());

		participante.setObservacao(participanteDto.getObservacao());

		participante.setData(this.dateFormat.parse(participanteDto.getData()));


		if (EnumUtils.isValidEnum(EstadoCivilEnum.class, participanteDto.getEstadoCivil())) {
			participante.setEstadoCivil(EstadoCivilEnum.valueOf(participanteDto.getEstadoCivil()));
		} else {
			result.addError(new ObjectError("estadoCivil", "estado Civil inválido."));
		}

		if (EnumUtils.isValidEnum(StatusEnum.class, participanteDto.getStatus())) {
			participante.setStatus(StatusEnum.valueOf(participanteDto.getStatus()));
		} else {
			result.addError(new ObjectError("status", "status inválido."));
		}

		if (EnumUtils.isValidEnum(SexoEnum.class, participanteDto.getSexo())) {
			participante.setSexo(SexoEnum.valueOf(participanteDto.getSexo()));
		} else {
			result.addError(new ObjectError("sexo", "sexo inválido."));
		}

		return participante;
	}

}
