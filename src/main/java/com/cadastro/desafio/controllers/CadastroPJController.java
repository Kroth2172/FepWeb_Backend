package com.cadastro.desafio.controllers;

import com.cadastro.desafio.dtos.CadastroPJDto;
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
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EmpresaService empresaService;

	public CadastroPJController() {
	}

	/**
	 * Cadastra uma pessoa jurídica no sistema.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * @return ResponseEntity<Response<CadastroPJDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PJ: {}", cadastroPJDto.toString());
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();

		validarDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto);
		Usuario usuario = this.converterDtoParaUsuario(cadastroPJDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.empresaService.persistir(empresa);
		usuario.setEmpresa(empresa);
		this.usuarioService.persistir(usuario);

		response.setData(this.converterCadastroPJDto(usuario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa ou usuário já existem na base de dados.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));

		this.usuarioService.buscarPorCpf(cadastroPJDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("usuario", "CPF já existente.")));

		this.usuarioService.buscarPorEmail(cadastroPJDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("usuario", "Email já existente.")));
	}

	/**
	 * Converte os dados do DTO para empresa.
	 * 
	 * @param cadastroPJDto
	 * @return Empresa
	 */
	private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());

		return empresa;
	}

	/**
	 * Converte os dados do DTO para usuário.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * @return Usuario
	 * @throws NoSuchAlgorithmException
	 */
	private Usuario converterDtoParaUsuario(CadastroPJDto cadastroPJDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Usuario usuario = new Usuario();
		usuario.setNome(cadastroPJDto.getNome());
		usuario.setEmail(cadastroPJDto.getEmail());
		usuario.setCpf(cadastroPJDto.getCpf());
		usuario.setPerfil(PerfilEnum.ROLE_ADMIN);
		usuario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()));

		return usuario;
	}

	/**
	 * Popula o DTO de cadastro com os dados do usuário e empresa.
	 * 
	 * @param usuario
	 * @return CadastroPJDto
	 */
	private CadastroPJDto converterCadastroPJDto(Usuario usuario) {
		CadastroPJDto cadastroPJDto = new CadastroPJDto();
		cadastroPJDto.setId(usuario.getId());
		cadastroPJDto.setNome(usuario.getNome());
		cadastroPJDto.setEmail(usuario.getEmail());
		cadastroPJDto.setCpf(usuario.getCpf());
		cadastroPJDto.setRazaoSocial(usuario.getEmpresa().getRazaoSocial());
		cadastroPJDto.setCnpj(usuario.getEmpresa().getCnpj());

		return cadastroPJDto;
	}

}
