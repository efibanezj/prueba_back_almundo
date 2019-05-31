package com.pruebaAlmundo.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.pruebaAlmundo.Dispatcher;
import com.pruebaAlmundo.modelo.Llamada;
import com.pruebaAlmundo.util.EstadosLlamada;
import com.pruebaAlmundo.util.Rol;

/**
 * Casos de prueba básicos para probar la funcionaldiad del proyecto
 * 
 * @author Fabian
 *
 */
public class DispatcherTest {

	Dispatcher callCenterPrincipal;

	/**
	 * Crea el despachador con 10 empleados
	 * 
	 * @throws InterruptedException
	 */
	@Before
	public void prepararAmbientePorDefecto() throws InterruptedException {

		try {
			callCenterPrincipal = new Dispatcher();
			callCenterPrincipal.setCantidadOperadores(6);
			callCenterPrincipal.setCantidadSupervisores(3);
			callCenterPrincipal.setCantidadDirectores(1);
			callCenterPrincipal.iniciarOperacion();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Esta prueba tiene como objetivo procesar 10 llamadas de forma concurrente,
	 * quedando con estado "Atendida".
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaDiezLlamadasConcurrentemente() throws InterruptedException, ExecutionException {

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos 10 llamadas
		for (int i = 0; i < 10; i++) {
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
	 * Esta prueba tiene como objetivo verifica que todas las llamadas se atiendan
	 * entre el rango de 5 a 10 segundos
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaLlamadasEntre5y10Segundos() throws InterruptedException, ExecutionException {

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos n llamadas
		for (int i = 0; i < 10; i++) {
			Llamada llamada = new Llamada("COL_TO_BRA_" + (i + 1));
			callCenterPrincipal.dispatchCall(llamada);
			listaLlamadas.add(llamada);
		}

		// Esperamos la finalización de todas las llamadas
		callCenterPrincipal.esperarFinalizacionLlamadas();

		// Recorremos la lista de llamadas y buscamos que todas las llamadas atendidas
		// duren entre 5 y 10 segundos
		int llamadasAtendidasEntre5y10 = 0;
		List<Llamada> listaLlamadasAtendidas = new ArrayList<>();
		for (Llamada llamada : listaLlamadas) {

			if (llamada.getEstado().getDescripcion().equalsIgnoreCase(EstadosLlamada.ATENDIDA.getDescripcion())) {
				listaLlamadasAtendidas.add(llamada);
			}

			if (llamada.getDuracionLlamada() >= 5 && llamada.getDuracionLlamada() <= 10) {
				llamadasAtendidasEntre5y10++;
			}
		}

		// Se verifica que exista tantas llamadas atendidas como las que se despacharon
		assertEquals(listaLlamadasAtendidas.size(), llamadasAtendidasEntre5y10);
	}

	/**
	 * Esta prueba tiene como objetivo verificar que la jerarquía en la atención de
	 * llamadas se atiend como se exige en los requerimientos
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void procesaLlamadasSegunJerarquia() throws InterruptedException, ExecutionException {

		// Condiciones iniciales-
		int CANTIDAD_LLAMADAS = 3;

		int CANTIDAD_OPERADORES = 1;
		int CANTIDAD_SUPERVISORES = 1;
		int CANTIDAD_DIRECTORES = 1;

		callCenterPrincipal = new Dispatcher();
		callCenterPrincipal.setCantidadOperadores(CANTIDAD_OPERADORES);
		callCenterPrincipal.setCantidadSupervisores(CANTIDAD_SUPERVISORES);
		callCenterPrincipal.setCantidadDirectores(CANTIDAD_DIRECTORES);
		callCenterPrincipal.iniciarOperacion();

		// Lista de llamadas a realizar
		List<Llamada> listaLlamadas = new ArrayList<>();

		// Despachamos n llamadas
		for (int i = 0; i < CANTIDAD_LLAMADAS; i++) {
			Llamada llamada = new Llamada("COL_TO_BRA_" + (i + 1));
			callCenterPrincipal.dispatchCall(llamada);
			listaLlamadas.add(llamada);
		}

		// Esperamos la finalización de todas las llamadas
		callCenterPrincipal.esperarFinalizacionLlamadas();

		// Se verifica que las llamas se despachara por quiesnes debian
		boolean primeraLlamadaOperador = listaLlamadas.get(0).getObservaciones()
				.contains(Rol.OPERADOR.getDescripcion());
		boolean segundaLlamadaSupervisor = listaLlamadas.get(1).getObservaciones()
				.contains(Rol.SUPERVISOR.getDescripcion());
		boolean terceraLlamadaDirector = listaLlamadas.get(2).getObservaciones()
				.contains(Rol.DIRECTOR.getDescripcion());

		assertTrue(primeraLlamadaOperador && segundaLlamadaSupervisor && terceraLlamadaDirector);
	}

}