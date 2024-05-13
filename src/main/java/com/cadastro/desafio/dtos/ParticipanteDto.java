package com.cadastro.desafio.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Getter
@Setter
@JsonIgnoreProperties
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteDto {

	private Optional<Long> id = Optional.empty();
	private Long usuarioId;
	private String codigo;
	private String codigoExterno;
	private String nome;
	private String email;
	private String cpf;
	private String sexo;
	private String estadoCivil;
	private String observacao;
	@NotEmpty(message = "Data não pode ser vazia.")
	private String data;
	private String status;

	@Override
	public String toString() {
		return "ParticipanteDto{" +
				"id=" + id +
				", usuarioId=" + usuarioId +
				", codigo='" + codigo + '\'' +
				", codigoExterno='" + codigoExterno + '\'' +
				", nome='" + nome + '\'' +
				", email='" + email + '\'' +
				", cpf='" + cpf + '\'' +
				", sexo='" + sexo + '\'' +
				", estadoCivil='" + estadoCivil + '\'' +
				", observacao='" + observacao + '\'' +
				", data='" + data + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
