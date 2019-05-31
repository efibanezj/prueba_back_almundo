package com.pruebaAlmundo.negocio;

import com.pruebaAlmundo.Dispatcher;
/**
 * Clase encargada de representar los equipos que atienden llamadas
 * @author Fabian
 *
 */
public abstract class EquipoAtencionAlCliente implements OperacionesLlamada {
	/**
	 * Referencia al despachador de llamadas
	 */
	protected Dispatcher callCenter;


}
