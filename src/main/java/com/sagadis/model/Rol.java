package com.sagadis.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_rol")
@Data
public class Rol {
	@Id
	private int id_rol;
	private String descripcion;
	private Boolean estado;
	private Date fecha_creacion;
	private Date fecha_modificacion;
}
