package com.pruebaAlmundo.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.pruebaAlmundo.Dispatcher;
import com.pruebaAlmundo.modelo.Llamada;
import com.pruebaAlmundo.util.EstadosLlamada;

/**
 * Casos de preuba para verificar funcionalidades y mejoras extras al programa
 * 
 * @author Fabian
 *
 */
public class DispatcherTestExtra {

	Dispatcher callCenterPrincipal;

	/**
	 * Prueba que tiene como objetivo atender todas las llamadas entrantes. La
	 * particularidad que tiene esta prueba es que hay menos empleados que llamadas
	 * recibidas. La solución que se planteó fue crear una cola adicional que se
	 * denominaría "De espera". Allí se alojarán las llamadas que no sean atendidas
	 * por ningún equipo. Luego, cada vez que una llamda se termina, se verifica
	 * priemro la cola de llamadas en espera antes de seguir con las llamadas
	 * entrantes.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaMasLlamadasConEmpleadosOcupados() throws InterruptedException, ExecutionException {

		// Condiciones iniciales-
		// Existen mas llamadas que trabajadores
		int CANTIDAD_LLAMADAS = 8;

		int CANTIDAD_OPERADORES = 2;
		int CANTIDAD_SUPERVISORES = 1;
		int CANTIDAD_DIRECTORES = 1;

		callCenterPrincipal = new Dispatcher();
		callCenterPrincipal.setCantidadOperadores(CANTIDAD_OPERADORES);
		callCenterPrincipal.setCantidadSupervisores(CANTIDAD_SUPERVISORES);
		callCenterPrincipal.setCantidadDirectores(CANTIDAD_DIRECTORES);
		callCenterPrincipal.iniciarOperacion();

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos 10 llamadas
		for (int i = 0; i < CANTIDAD_LLAMADAS; i++) {
			Llamada llamada = new Llamada("COL_TO_BRA_" + (i + 1));
			callCenterPrincipal.dispatchCall(llamada);
			listaLlamadas.add(llamada);
		}

		// Esperamos la finalización de todas las llamadas
		callCenterPrincipal.esperarFinalizacionLlamadas();

		// Recorremos la lista de llamadas y buscamos alguna llamada que no esté
		// atendida
		int llamadasAtendidas = 0;
		for (Llamada llamada : listaLlamadas) {
			if (llamada.getEstado().getDescripcion().equalsIgnoreCase(EstadosLlamada.ATENDIDA.getDescripcion())) {
				llamadasAtendidas++;
			}
		}

		// Se verifica que exista tantas llamadas atendidas como las que se despacharon
		assertEquals(listaLlamadas.size(), llamadasAtendidas);
	}

	/**
	 * Prueba que tiene como objetivo procesa mas de 10 llamadas con una cola de
	 * espera pequeña. En este caso, el requerimiento, determina que las llamadas
	 * entrantes serán limitadas, por lo que si las colas de llamadas entrantes,
	 * pendientes y en curso se llenan, las llamadas se rechazan.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaMasLlamadasDe10yColaDeEsperaPequeña() throws InterruptedException, ExecutionException {

		// Condiciones iniciales-
		// Existen mas llamadas que capacidad de recepción en las llamadas
		int CANTIDAD_LLAMADAS = 16;

		int CANTIDAD_OPERADORES = 1;
		int CANTIDAD_SUPERVISORES = 1;
		int CANTIDAD_DIRECTORES = 1;

		int CAPACIDAD_LLAMADAS_ENTRANTES = 6;
		int CAPACIDAD_LLAMADAS_EN_CURSO = 3;
		int CAPACIDAD_LLAMADAS_EN_ESPERA = 6;

		callCenterPrincipal = new Dispatcher(CAPACIDAD_LLAMADAS_ENTRANTES, CAPACIDAD_LLAMADAS_EN_CURSO,
				CAPACIDAD_LLAMADAS_EN_ESPERA);
		callCenterPrincipal.setCantidadOperadores(CANTIDAD_OPERADORES);
		callCenterPrincipal.setCantidadSupervisores(CANTIDAD_SUPERVISORES);
		callCenterPrincipal.setCantidadDirectores(CANTIDAD_DIRECTORES);
		callCenterPrincipal.iniciarOperacion();

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos 10 llamadas
		for (int i = 0; i < CANTIDAD_LLAMADAS; i++) {
			Llamada llamada = new Llamada("COL_TO_BRA_" + (i + 1));
			callCenterPrincipal.dispatchCall(llamada);
			listaLlamadas.add(llamada);
		}

		// Esperamos la finalización de todas las llamadas
		callCenterPrincipal.esperarFinalizacionLlamadas();

		// Recorremos la lista de llamadas y buscamos alguna llamada que no esté
		// atendida
		int llamadasAtendidas = 0;
		int llamadasRechazadas = 0;
		for (Llamada llamada : listaLlamadas) {
			if (llamada.getEstado().getDescripcion().equalsIgnoreCase(EstadosLlamada.ATENDIDA.getDescripcion())) {
				llamadasAtendidas++;
			} else if (llamada.getEstado().getDescripcion()
					.equalsIgnoreCase(EstadosLlamada.RECHAZADA.getDescripcion())) {
				llamadasRechazadas++;
			}
		}

		// Se verifica que exista tantas llamadas atendidas como las que se despacharon
		assertEquals(listaLlamadas.size(), llamadasAtendidas + llamadasRechazadas);
	}

	
	/**
	 * Prueba que tiene como objetivo procesa mas de 10 llamadas con una cola de
	 * espera grande. En este caso,las llamadas se acumulan en la cola de espera
	 * hasta que un empleado esté disponible para atender la llamada. En este caso
	 * como las colas tienen mas capacidad, no se rechaza ninguna llamada
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaMasLlamadasDe10yColaDeEsperaGrande() throws InterruptedException, ExecutionException {

		// Condiciones iniciales-
		// Existen mas llamadas que capacidad de recepción en las llamadas
		int CANTIDAD_LLAMADAS = 12;

		int CANTIDAD_OPERADORES = 1;
		int CANTIDAD_SUPERVISORES = 1;
		int CANTIDAD_DIRECTORES = 1;

		int CAPACIDAD_LLAMADAS_ENTRANTES = 30;
		int CAPACIDAD_LLAMADAS_EN_CURSO = 3;
		int CAPACIDAD_LLAMADAS_EN_ESPERA = 30;

		callCenterPrincipal = new Dispatcher(CAPACIDAD_LLAMADAS_ENTRANTES, CAPACIDAD_LLAMADAS_EN_CURSO,
				CAPACIDAD_LLAMADAS_EN_ESPERA);
		callCenterPrincipal.setCantidadOperadores(CANTIDAD_OPERADORES);
		callCenterPrincipal.setCantidadSupervisores(CANTIDAD_SUPERVISORES);
		callCenterPrincipal.setCantidadDirectores(CANTIDAD_DIRECTORES);
		callCenterPrincipal.iniciarOperacion();

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos 10 llamadas
		for (int i = 0; i < CANTIDAD_LLAMADAS; i++) {
			Llamada llamada = new Llamada("COL_TO_BRA_" + (i + 1));
			callCenterPrincipal.dispatchCall(llamada);
			listaLlamadas.add(llamada);
		}

		// Esperamos la finalización de todas las llamadas
		callCenterPrincipal.esperarFinalizacionLlamadas();

		// Recorremos la lista de llamadas y buscamos alguna llamada que no esté
		// atendida
		int llamadasAtendidas = 0;
		for (Llamada llamada : listaLlamadas) {
			if (llamada.getEstado().getDescripcion().equalsIgnoreCase(EstadosLlamada.ATENDIDA.getDescripcion())) {
				llamadasAtendidas++;
			}
		}

		// Se verifica que exista tantas llamadas atendidas como las que se despacharon
		assertEquals(listaLlamadas.size(), llamadasAtendidas);
	}

}