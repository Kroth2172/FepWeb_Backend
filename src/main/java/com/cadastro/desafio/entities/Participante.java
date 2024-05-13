package com.cadastro.desafio.entities;

import com.cadastro.desafio.enums.EstadoCivilEnum;
import com.cadastro.desafio.enums.SexoEnum;
import com.cadastro.desafio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participante")
public class Participante implements Serializable {
	
	private static final long serialVersionUID = 6524560251526772839L;

	@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;

	@Column(name = "codigo", nullable = true)
	private String codigo;

	@Column(name = "codigoExterno", nullable = true)
	private String codigoExterno;

	@Column(name = "nome", nullable = true)
	private String nome;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "cpf", nullable = false)
	private String cpf;

	@Enumerated(EnumType.STRING)
	@Column(name = "sexo", nullable = false)
	private SexoEnum sexo;

	@Enumerated(EnumType.STRING)
	@Column(name = "estadoCivil", nullable = false)
	private EstadoCivilEnum estadoCivil;

	@Column(name = "observacao", nullable = false)
	private String observacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StatusEnum status;

	@Column(name = "data_criacao", nullable = false)
	private Date dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private Date dataAtualizacao;

	@ManyToOne(fetch = FetchType.EAGER)
	private Usuario usuario;
	
	@PreUpdate
    public void preUpdate() {
        dataAtualizacao = new Date();
    }
     
    @PrePersist
    public void prePersist() {
        final Date atual = new Date();
        dataCriacao = atual;
        dataAtualizacao = atual;
    }
}
