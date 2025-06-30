package com.sagadis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sagadis.model.Asistencia;
@Repository
public interface IAsistenciaRepository extends JpaRepository<Asistencia, Integer>{
	@Procedure(procedureName = "spU_Registrar_Marcacion_Mensaje")
	void registrarMarcacion(@Param("p_id_trabajador") Integer idTrabajador);
}
