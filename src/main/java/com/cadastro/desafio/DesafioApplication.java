package com.cadastro.desafio;

import com.cadastro.desafio.entities.Empresa;
import com.cadastro.desafio.entities.Usuario;
import com.cadastro.desafio.enums.PerfilEnum;
import com.cadastro.desafio.repositories.EmpresaRepository;
import com.cadastro.desafio.repositories.ParticipanteRepository;
import com.cadastro.desafio.repositories.UsuarioRepository;
import com.cadastro.desafio.utils.PasswordUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@EnableCaching
@SpringBootApplication
public class DesafioApplication {

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ParticipanteRepository participanteRepository;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(DesafioApplication.class, args);
	}

	@Component
	public class CommandLineAppStartupRunner implements CommandLineRunner {

		@Override
		public void run(String...args) throws Exception {
			Empresa empresa = new Empresa();
			empresa.setRazaoSocial("Empresa Teste");
			empresa.setCnpj("04193834000111");
			empresaRepository.save(empresa);

			Usuario usuarioAdmin = new Usuario();
			usuarioAdmin.setCpf("25164061422");
			usuarioAdmin.setEmail("admin@empresa.com");
			usuarioAdmin.setNome("Administrador teste");
			usuarioAdmin.setPerfil(PerfilEnum.ROLE_ADMIN);
			usuarioAdmin.setSenha(PasswordUtils.gerarBCrypt("123456"));
			usuarioAdmin.setEmpresa(empresa);
			usuarioRepository.save(usuarioAdmin);

			Usuario usuario = new Usuario();
			usuario.setCpf("09943636211");
			usuario.setEmail("usuario@empresa.com");
			usuario.setNome("Usuario teste");
			usuario.setPerfil(PerfilEnum.ROLE_USUARIO);
			usuario.setSenha(PasswordUtils.gerarBCrypt("123456"));
			usuario.setEmpresa(empresa);
			usuarioRepository.save(usuario);

			empresaRepository.findAll().forEach(System.out::println);
			usuarioRepository.findByEmpresaId(empresa.getId()).forEach(System.out::println);
		}
	}
}
