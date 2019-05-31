package com.pruebaAlmundo.util;

/**
 * Representa los roles que puede tener un empleado
 * 
 * @author Fabian
 *
 */
public enum Rol {

	OPERADOR(1, "OPERADOR"), SUPERVISOR(2, "SUPERVISOR"), DIRECTOR(3, "DIRECTOR");
	/**
	 * Identificador
	 */
	private int idRol;
	/**
	 * Descripción
	 */
	private String descripcion;

	/**
	 * 
	 * @param idRol
	 * @param descripcion
	 */
	private Rol(int idRol, String descripcion) {
		this.setIdRol(idRol);
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
		return idRol;
	}

	/**
	 * 
	 * @param idRol
	 */
	public void setIdRol(int idRol) {
		this.idRol = idRol;
	}

}