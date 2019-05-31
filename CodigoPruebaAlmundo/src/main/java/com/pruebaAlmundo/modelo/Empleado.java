package com.pruebaAlmundo.modelo;

import com.pruebaAlmundo.util.Rol;
/**
 * Clase que representa un empleado
 * @author Fabian
 *
 */
public class Empleado {
	/**
	 * Identificador
	 */
	private int idEmpleado;
	/**
	 * Rol del empleado
	 */
	private Rol rol;

	/**
	 * 
	 * @param idEmpleado
	 * @param rol
	 */
	public Empleado(int idEmpleado, Rol rol) {
		this.idEmpleado = idEmpleado;
		this.rol = rol;
	}
	/**
	 * 
	 * @return
	 */
	public int getIdEmpleado() {
		return idEmpleado;
	}
	/**
	 * 
	 * @param idEmpleado
	 */
	public void setIdEmpleado(int idEmpleado) {
		this.idEmpleado = idEmpleado;
	}
	/**
	 * 
	 * @return
	 */
	public Rol getRol() {
		return rol;
	}
	/**
	 * 
	 * @param rol
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

}