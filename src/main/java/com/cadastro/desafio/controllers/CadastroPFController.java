package com.cadastro.desafio.controllers;

import com.cadastro.desafio.dtos.CadastroPFDto;
import com.cadastro.desafio.entities.Empresa;
import com.cadastro.desafio.entities.Usuario;
import com.cadastro.desafio.enums.PerfilEnum;
import com.cadastro.desafio.response.Response;
import com.cadastro.desafio.services.EmpresaService;
import com.cadastro.desafio.services.UsuarioService;
import com.cadastro.desafio.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);
	
	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private UsuarioService usuarioService;

	public CadastroPFController() {
	}

	/**
	 * Cadastra um usuário pessoa física no sistema.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 * @return ResponseEntity<Response<CadastroPFDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PF: {}", cadastroPFDto.toString());
		Response<CadastroPFDto> response = new Response<CadastroPFDto>();

		validarDadosExistentes(cadastroPFDto, result);
		Usuario usuario = this.converterDtoParaUsuario(cadastroPFDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		empresa.ifPresent(emp -> usuario.setEmpresa(emp));
		this.usuarioService.persistir(usuario);

		response.setData(this.converterCadastroPFDto(usuario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa está cadastrada e se o usuário não existe na base de dados.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada."));
		}
		
		this.usuarioService.buscarPorCpf(cadastroPFDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("usuario", "CPF já existente.")));

		this.usuarioService.buscarPorEmail(cadastroPFDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("usuario", "Email já existente.")));
	}

	/**
	 * Converte os dados do DTO para usuário.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 * @return Usuario
	 * @throws NoSuchAlgorithmException
	 */
	private Usuario converterDtoParaUsuario(CadastroPFDto cadastroPFDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Usuario usuario = new Usuario();
		usuario.setNome(cadastroPFDto.getNome());
		usuario.setEmail(cadastroPFDto.getEmail());
		usuario.setCpf(cadastroPFDto.getCpf());
		usuario.setPerfil(PerfilEnum.ROLE_USUARIO);
		usuario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		return usuario;
	}

	/**
	 * Popula o DTO de cadastro com os dados do usuário e empresa.
	 * 
	 * @param usuario
	 * @return CadastroPFDto
	 */
	private CadastroPFDto converterCadastroPFDto(Usuario usuario) {
		CadastroPFDto cadastroPFDto = new CadastroPFDto();
		cadastroPFDto.setId(usuario.getId());
		cadastroPFDto.setNome(usuario.getNome());
		cadastroPFDto.setEmail(usuario.getEmail());
		cadastroPFDto.setCpf(usuario.getCpf());
		cadastroPFDto.setCnpj(usuario.getEmpresa().getCnpj());
		return cadastroPFDto;
	}

}
