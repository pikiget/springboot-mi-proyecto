package com.sagadis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sagadis.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer>{
	// Repositorio CORRECTO
	Optional<Usuario> findByUsernameAndContrasenia(String username, String contrasenia);

	Optional<Usuario> findByUsername(String username);

	Optional<Usuario> findById(Integer id);
	
	@Procedure(procedureName = "spU_Eliminar_Usuario")
	 void eliminarUsuario(@Param("p_id_usuario") Integer id_usuario);

}
