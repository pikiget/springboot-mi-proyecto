package com.sagadis.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_asistencia")
@Data
public class Asistencia {
	@Id
	private int id_asistencia;
	private Integer id_trabajador;
	private Date fecha;
	private String hora;
	private String tipo_marca;
	private String desc_marc;
	private Boolean estado = true;
	private Date fecha_creacion;
	private Date fecha_modificacion;
	
	@ManyToOne
	@JoinColumn(name = "id_trabajador", insertable = false, updatable = false)
	private Trabajador objTrabajador;
}