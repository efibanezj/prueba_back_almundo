package com.pruebaAlmundo.modelo;

import com.pruebaAlmundo.util.EstadosLlamada;
/**
 * Clase que representa una llamada 
 * @author Fabian
 *
 */
public class Llamada {
	/**
	 * Identificador
	 */
	private String idLlamada;
	/**
	 * Estado
	 */
	private EstadosLlamada estado;
	/**
	 * Duracion en segundos
	 */
	private long duracionLlamada;
	/**
	 * Observaciones varias
	 */
	private String observaciones;

	/**
	 * 
	 * @param idLlamada
	 */
	public Llamada(String idLlamada) {
		this.idLlamada = idLlamada;
		this.estado = EstadosLlamada.SIN_ATENDER;
	}
	/**
	 * 
	 * @return
	 */
	public EstadosLlamada getEstado() {
		return estado;
	}
	/**
	 * 
	 * @param estado
	 */
	public void setEstado(EstadosLlamada estado) {
		this.estado = estado;
	}
	/**
	 * 
	 * @return
	 */
	public String getIdLlamada() {
		return idLlamada;
	}
	/**
	 * 
	 * @return
	 */
	public long getDuracionLlamada() {
		return duracionLlamada;
	}
	/**
	 * 
	 * @param duracionLlamada
	 */
	public void setDuracionLlamada(long duracionLlamada) {
		this.duracionLlamada = duracionLlamada;
	}
	/**
	 * 
	 * @return
	 */
	public String getObservaciones() {
		return observaciones;
	}
	/**
	 * 
	 * @param observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}
