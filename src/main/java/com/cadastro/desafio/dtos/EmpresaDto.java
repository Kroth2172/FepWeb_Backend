package com.cadastro.desafio.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDto {
	
	private Long id;
	private String razaoSocial;
	private String cnpj;

	@Override
	public String toString() {
		return "EmpresaDto{" +
				"id=" + id +
				", razaoSocial='" + razaoSocial + '\'' +
				", cnpj='" + cnpj + '\'' +
				'}';
	}
}
