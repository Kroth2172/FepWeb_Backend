package com.cadastro.desafio.security.impl;

import com.cadastro.desafio.entities.Participante;
import com.cadastro.desafio.repositories.ParticipanteRepository;
import com.cadastro.desafio.services.ParticipanteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipanteServiceImpl implements ParticipanteService {

	private static final Logger log = LoggerFactory.getLogger(ParticipanteServiceImpl.class);

	@Autowired
	private ParticipanteRepository participanteRepository;

	public Page<Participante> buscarPorUsuarioId(Long usuarioId, PageRequest pageRequest) {
		log.info("Buscando participantes para o usuário ID {}", usuarioId);
		return this.participanteRepository.findByUsuarioId(usuarioId, pageRequest);
	}

	public List<Participante> buscarTodosPorUsuarioId(Long usuarioId) {
		log.info("Buscando todos os participantes para o usuário ID {}", usuarioId);
		return this.participanteRepository.findByUsuarioIdOrderByDataDesc(usuarioId);
	}
	
	@Cacheable("participantePorId")
	public Optional<Participante> buscarPorId(Long id) {
		log.info("Buscando um participante pelo ID {}", id);
		return this.participanteRepository.findById(id);
	}
	
	@CachePut("participantePorId")
	public Participante persistir(Participante participante) {
		log.info("Persistindo o participante: {}", participante);
		return this.participanteRepository.save(participante);
	}
	
	public void remover(Long id) {
		log.info("Removendo o participante ID {}", id);
		this.participanteRepository.deleteById(id);
	}

	@Override
	public Optional<Participante> buscarUltimoPorUsuarioId(Long usuarioId) {
		log.info("Buscando o último participante por ID de usuário {}", usuarioId);
		return Optional.ofNullable(
				this.participanteRepository.findFirstByUsuarioIdOrderByDataCriacaoDesc(usuarioId));
	}

}
