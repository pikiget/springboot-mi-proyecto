package com.sagadis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sagadis.model.Trabajador;

@Repository
public interface ITrabajadorRepository extends JpaRepository<Trabajador, Integer> {
	Optional<Trabajador> findById(Integer id);

	@Procedure(procedureName = "spU_Eliminar_Trabajador")
	void eliminarTrabajador(@Param("p_id_trabajador") Integer id_trabajador);

	@Query(value = "CALL sp_Listado_Trabajador_ActivoSinUsuario()", nativeQuery = true)
	List<Trabajador> listarTrabajadoresActivosSinUsuario();

}
