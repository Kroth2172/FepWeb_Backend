package com.cadastro.desafio.security.impl;

import com.cadastro.desafio.entities.Usuario;
import com.cadastro.desafio.repositories.UsuarioRepository;
import com.cadastro.desafio.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Usuario persistir(Usuario usuario) {
		log.info("Persistindo usuário: {}", usuario);
		return this.usuarioRepository.save(usuario);
	}
	
	public Optional<Usuario> buscarPorCpf(String cpf) {
		log.info("Buscando usuário pelo CPF {}", cpf);
		return Optional.ofNullable(this.usuarioRepository.findByCpf(cpf));
	}
	
	public Optional<Usuario> buscarPorEmail(String email) {
		log.info("Buscando usuário pelo email {}", email);
		return Optional.ofNullable(this.usuarioRepository.findByEmail(email));
	}
	
	public Optional<Usuario> buscarPorId(Long id) {
		log.info("Buscando usuário pelo ID {}", id);
		return this.usuarioRepository.findById(id);
	}

	@Override
	public List<Usuario> buscarPorEmpresaId(Long id) {
		log.info("Buscando usuários pela empresa ID {}", id);
		return usuarioRepository.findByEmpresaId(id);
	}

}
