package com.sagadis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sagadis.model.Horario;

@Repository
public interface IHorarioRespository extends JpaRepository<Horario, Integer>{
	@Procedure(procedureName = "spU_Eliminar_Horario")
	 void eliminarHorario(@Param("p_id_horario") Integer id_horario);
}
