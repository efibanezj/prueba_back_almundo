package com.pruebaAlmundo.negocio;

import com.pruebaAlmundo.modelo.Llamada;

/**
 * Interface que identificalas operaciones sobre una llamada
 * 
 * @author Fabian
 *
 */
public interface OperacionesLlamada {
	/**
	 * Método que procesa una llamada
	 * 
	 * @param llamada
	 * @return
	 */
	public Llamada procesarLlamada(Llamada llamada);

	/**
	 * Asigna el siguiente equipo para la atención de llamadas
	 * 
	 * @param areaAtencionAlCliente
	 */
	public void setEquipoSiguienteNivel(OperacionesLlamada areaAtencionAlCliente);

}
