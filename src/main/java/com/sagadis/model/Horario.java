package com.sagadis.model;

import java.sql.Date;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_horario")
@Data
public class Horario {
	@Id
	private int id_horario;
	private String descripcion;
	private LocalTime hora_inicio;
	private LocalTime hora_fin;
	private int tolerancia_ingreso;
	private int tolerancia_salida;
	private Boolean estado = true;
	private Date fecha_creacion;
	private Date fecha_modificacion;
}
