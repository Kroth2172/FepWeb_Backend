package com.cadastro.desafio.repositories;

import com.cadastro.desafio.entities.Participante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;

@Transactional(readOnly = true)
@NamedQueries({
		@NamedQuery(name = "ParticipanteRepository.findByUsuarioId",
				query = "SELECT lanc FROM Participante lanc WHERE lanc.usuario.id = :usuarioId") })
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

	List<Participante> findByUsuarioId(@Param("usuarioId") Long usuarioId);

	Page<Participante> findByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);

	Participante findFirstByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

	List<Participante> findByUsuarioIdOrderByDataDesc(Long usuarioId);

}
