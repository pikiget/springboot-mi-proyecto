package com.sagadis.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_trabajador")
@Data
public class Trabajador {
	@Id
	private int id_trabajador;
	private Integer id_horario;
	private String nombres;
	private String apellidos;
	private Date fecha_nacimiento;
	private String numero_documento;
	private String telefono;
	private String correo_electronico;
	private String cargo;
	private String area;
	private Boolean estado;
	private Date fecha_creacion;
	private Date fecha_modificacion;
	
	@ManyToOne
	@JoinColumn(name = "id_horario", insertable = false, updatable = false)
	private Horario objHorario;
}

