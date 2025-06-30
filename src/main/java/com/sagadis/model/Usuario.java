package com.sagadis.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_usuario")
@Data
public class Usuario {
	@Id
	private int id_usuario;
	private Integer id_rol;
	private Integer id_trabajador;
	private String username;
	private String contrasenia;
	private Boolean estado; 
	private Date fecha_creacion;
	private Date fecha_modificacion;
	
	//Llaves foraneas
	@ManyToOne
	@JoinColumn(name = "id_rol", insertable = false, updatable = false)
	private Rol objRol;
	
	@OneToOne
	@JoinColumn(name = "id_trabajador", insertable = false, updatable = false)
	private Trabajador objTrabajador;
}
