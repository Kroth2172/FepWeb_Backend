package com.cadastro.desafio.services;

import com.cadastro.desafio.entities.Participante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipanteService {

	/**
	 * Retorna uma lista paginada de participantes de um determinado usuário.
	 * 
	 * @param usuarioId
	 * @param pageRequest
	 * @return Page<Participante>
	 */
	Page<Participante> buscarPorUsuarioId(Long usuarioId, PageRequest pageRequest);
	
	/**
	 * Retorna um participante por ID.
	 * 
	 * @param id
	 * @return Optional<Participante>
	 */
	Optional<Participante> buscarPorId(Long id);
	
	/**
	 * Persiste um participante na base de dados.
	 * 
	 * @param participante
	 * @return Participante
	 */
	Participante persistir(Participante participante);
	
	/**
	 * Remove um participante da base de dados.
	 * 
	 * @param id
	 */
	void remover(Long id);

	/**
	 * Retorna o último participante por ID de usuário.
	 *
	 * @param usuarioId
	 * @return Optional<Participante>
	 */
    Optional<Participante> buscarUltimoPorUsuarioId(Long usuarioId);

	/**
	 * Retorna uma lista com todos os participantes de um determinado usuário.
	 *
	 * @param usuarioId
	 * @return List<Participante>
	 */
    List<Participante> buscarTodosPorUsuarioId(Long usuarioId);
}
