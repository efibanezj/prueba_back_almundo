package com.pruebaAlmundo.negocio;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pruebaAlmundo.Dispatcher;
import com.pruebaAlmundo.modelo.Empleado;
import com.pruebaAlmundo.modelo.Llamada;
import com.pruebaAlmundo.util.EstadosLlamada;
import com.pruebaAlmundo.util.Rol;

/**
 * Clase que identifica a un equipo de operadores
 * 
 * @author Fabian
 *
 */
public class EquipoOperadores extends EquipoAtencionAlCliente {

	private static final Logger logger = LoggerFactory.getLogger(EquipoOperadores.class);
	/**
	 * Siguiente equipo de atencion de llamadas
	 */
	private OperacionesLlamada equipoSiguienteNivel;
	/**
	 * Operadores disponibles
	 */
	private Queue<Empleado> operadoresDisponibles;

	/**
	 * Constructor que recibe la cantidad de empleados que va a tener y la
	 * referencia al despachador de llamadas
	 * 
	 * @param cantidadEmpleados
	 * @param callCenter
	 */
	public EquipoOperadores(int cantidadEmpleados, Dispatcher callCenter) {
		this.callCenter = callCenter;
		operadoresDisponibles = new LinkedList<>();
		for (int i = 0; i < cantidadEmpleados; i++) {
			operadoresDisponibles.offer(new Empleado(i + 1, Rol.OPERADOR));
		}
	}

	@Override
	public Llamada procesarLlamada(Llamada llamada) {
		llamada.setEstado(EstadosLlamada.EN_PROGRESO);
		if (!operadoresDisponibles.isEmpty()) {
			Empleado empleadoAsignado = operadoresDisponibles.poll();

			// Verifica si en curso hay mas del l�mite establecido
			boolean ingresarLlamadaEnCurso = callCenter.getColallamadasEnCurso().offer(llamada);
			if (ingresarLlamadaEnCurso) {
				return atenderLlamada(empleadoAsignado, llamada);
			} else {

				logger.warn("La llamada " + llamada.getIdLlamada() + " est� en espera a ser atendida...");
				callCenter.getColallamadasEnEspera().offer(llamada);
				return callCenter.procesarSiguienteLlamada();
			}
		} else {
			return equipoSiguienteNivel.procesarLlamada(llamada);
		}
	}

	/**
	 * M�todo que atiene la llamada
	 * 
	 * @param empleadoAsignado
	 * @param llamada
	 * @return
	 */
	private Llamada atenderLlamada(Empleado empleadoAsignado, Llamada llamada) {

		CompletableFuture<Llamada> atendiendoLlamada = new CompletableFuture<Llamada>();

		Thread hiloLlamada = new Thread(() -> {
			logger.info("La llamada " + llamada.getIdLlamada() + " est� siendo atendida por el "
					+ empleadoAsignado.getRol().getDescripcion() + " " + empleadoAsignado.getIdEmpleado() + "...");
			try {
				Random random = new Random();
				long duracionLlamada = 1000 * random.ints(1, 5, 11).findFirst().getAsInt();
				Thread.sleep(duracionLlamada);
				llamada.setDuracionLlamada(duracionLlamada / 1000);
				llamada.setEstado(EstadosLlamada.ATENDIDA);
				llamada.setObservaciones(
						empleadoAsignado.getRol().getDescripcion() + " " + empleadoAsignado.getIdEmpleado());
				logger.info("La llamada " + llamada.getIdLlamada() + " fu� atendida por el "
						+ empleadoAsignado.getRol().getDescripcion() + " " + empleadoAsignado.getIdEmpleado()
						+ ". Duraci�n llamada:" + llamada.getDuracionLlamada() + " segundos.");
				callCenter.getColallamadasEnCurso().remove(llamada);
				callCenter.getColallamadasAtendidas().add(llamada);
				this.operadoresDisponibles.add(empleadoAsignado);
				callCenter.procesarSiguienteLlamada();
			} catch (Exception e) {
				e.printStackTrace();
			}
			atendiendoLlamada.complete(llamada);
		});

		hiloLlamada.start();
		return llamada;
	}

	@Override
	public void setEquipoSiguienteNivel(OperacionesLlamada equipoSiguienteNivel) {
		this.equipoSiguienteNivel = equipoSiguienteNivel;
	}

}
