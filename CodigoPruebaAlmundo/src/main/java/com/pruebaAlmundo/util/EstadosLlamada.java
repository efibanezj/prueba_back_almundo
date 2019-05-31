package com.pruebaAlmundo.util;

/**
 * Representa los posibles estados de una llamada
 * 
 * @author Fabian
 *
 */
public enum EstadosLlamada {

	SIN_ATENDER(1, "Sin atender"), EN_PROGRESO(2, "En progreso"), ATENDIDA(3, "Atendida"), RECHAZADA(4, "Rechazada");
	/**
	 * Identificador
	 */
	private int idEstado;
	/**
	 * Descripcion
	 */
	private String descripcion;

	/**
	 * 
	 * @param idEstado
	 * @param descripcion
	 */
	private EstadosLlamada(int idEstado, String descripcion) {
		this.setIdRol(idEstado);
		this.setDescripcion(descripcion);
	}

	/**
	 * 
	 * @return
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * 
	 * @param descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * 
	 * @return
	 */
	public int getIdRol() {
		return idEstado;
	}

	/**
	 * 
	 * @param idRol
	 */
	public void setIdRol(int idRol) {
		this.idEstado = idRol;
	}

}