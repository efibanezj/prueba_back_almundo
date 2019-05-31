package com.pruebaAlmundo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pruebaAlmundo.modelo.Llamada;
import com.pruebaAlmundo.negocio.EquipoAtencionAlCliente;
import com.pruebaAlmundo.negocio.EquipoDirectores;
import com.pruebaAlmundo.negocio.EquipoOperadores;
import com.pruebaAlmundo.negocio.EquipoSupervisores;
import com.pruebaAlmundo.util.EstadosLlamada;

/**
 * Clase encargada de manejar las llamadas entrantes y despacharas a los
 * empleados para ser atendidas
 * 
 * @author Fabian
 *
 */
public class Dispatcher {

	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	/**
	 * 
	 */
	private static int CAPACIDAD_LLAMADAS_ENTRANTES_DEFECTO = 10;
	/**
	 * 
	 */
	private static int CAPACIDAD_LLAMADAS_EN_CURSO_DEFECTO = 10;
	/**
	 * 
	 */
	private static int CAPACIDAD_LLAMADAS_EN_ESPERA_DEFECTO = 20;

	/**
	 * Indicador de operacion. Permite saber cuando teminan todas las llamadas
	 */
	private CompletableFuture<Llamada> indicadorEnOperacion = new CompletableFuture<Llamada>();

	/**
	 * Objeto que atiene las llamadas
	 */
	private EquipoAtencionAlCliente equipoAtencionAlCliente;

	/**
	 * Cola llamadas entrantes
	 */
	private Queue<Llamada> colaLlamadasEntrantes;
	/**
	 * Cola de las llamadas en espera
	 */
	private Queue<Llamada> colallamadasEnEspera;
	/**
	 * Cola de las llamas en curso
	 */
	private Queue<Llamada> colallamadasEnCurso;
	/**
	 * Cola de las llamadas antendidas
	 */
	private Queue<Llamada> colallamadasAtendidas;

	/**
	 * Cantidad de empleados con el rol de operadores
	 */
	private int cantidadOperadores;
	/**
	 * Cantidad de empleados con el rol de supervisores
	 */
	private int cantidadSupervisores;
	/**
	 * Cantidad de empleados con el rol de directores
	 */
	private int cantidadDirectores;

	/**
	 * Constructor que tomas los valores por defecto para el tamaño de las colas de
	 * las llamadas
	 */
	public Dispatcher() {
		colaLlamadasEntrantes = new ArrayBlockingQueue<>(CAPACIDAD_LLAMADAS_ENTRANTES_DEFECTO);
		colallamadasEnCurso = new ArrayBlockingQueue<>(CAPACIDAD_LLAMADAS_EN_CURSO_DEFECTO);
		colallamadasEnEspera = new ArrayBlockingQueue<>(CAPACIDAD_LLAMADAS_EN_ESPERA_DEFECTO);
		colallamadasAtendidas = new LinkedList<>();
	}

	/**
	 * Constructor que recibe la capacidad de las colas de las llamadas
	 * 
	 * @param capacidadEntrandes
	 * @param capacidadEnCurso
	 * @param capacidadEnEspera
	 */
	public Dispatcher(int capacidadEntrandes, int capacidadEnCurso, int capacidadEnEspera) {
		colaLlamadasEntrantes = new ArrayBlockingQueue<>(capacidadEntrandes);
		colallamadasEnCurso = new ArrayBlockingQueue<>(capacidadEnCurso);
		colallamadasEnEspera = new ArrayBlockingQueue<>(capacidadEnEspera);
		colallamadasAtendidas = new LinkedList<>();
	}

	/**
	 * Método que define la cadena de responsabilidades para atender las llamadas
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void definirJerarquiaAtencion() throws InterruptedException, ExecutionException {

		EquipoDirectores equipoDirec = new EquipoDirectores(cantidadDirectores, this);
		EquipoSupervisores equipoSuperv = new EquipoSupervisores(cantidadSupervisores, this);
		EquipoOperadores equipoOper = new EquipoOperadores(cantidadOperadores, this);

		equipoOper.setEquipoSiguienteNivel(equipoSuperv);
		equipoSuperv.setEquipoSiguienteNivel(equipoDirec);

		// Se define que equipo inicia la cadena de responsabilidad
		equipoAtencionAlCliente = equipoOper;

	}

	/**
	 * Imprime el inicio de la aplicación
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void iniciarOperacion() throws InterruptedException, ExecutionException {
		definirJerarquiaAtencion();
		logger.info("------------------------------------------");
		logger.info("El CallCenter ha iniciado operación.");
		logger.info("Tiene " + (cantidadOperadores + cantidadSupervisores + cantidadDirectores) + " empleados.");
		logger.info("Listo para atender llamadas.");
		logger.info("------------------------------------------");
	}

	/**
	 * Método principal que despacha las llamadas
	 * 
	 * @param llamada
	 * @return
	 */
	public Llamada dispatchCall(Llamada llamada) {

		if (recibirLlamada(llamada)) {
			llamada = procesarSiguienteLlamada();
		} else {
			logger.error("La llamada " + llamada.getIdLlamada()
					+ " fué rechazada porque se superó la capacidad de las colas de entrada y de espera.");
			llamada.setEstado(EstadosLlamada.RECHAZADA);
		}
		return llamada;
	}

	/**
	 * Método que procesa la siguiente llamada. Ya sea desde la cola de llamadass
	 * entrante como la de llamadas pendientes
	 * 
	 * @return
	 */
	public Llamada procesarSiguienteLlamada() {
		Llamada llamadaSiguiente = obtenerLlamadaSiguiente();
		if (llamadaSiguiente != null) {
			llamadaSiguiente = equipoAtencionAlCliente.procesarLlamada(llamadaSiguiente);
		} else {
			if (colallamadasEnEspera.isEmpty() && colallamadasEnCurso.isEmpty() && colaLlamadasEntrantes.isEmpty()) {
				indicadorEnOperacion.complete(llamadaSiguiente);
			}
		}

		return llamadaSiguiente;
	}

	/**
	 * Método que busca de alguna de las colas la llamada siguiente. Prioriza las pendientes.
	 * @return
	 */
	private Llamada obtenerLlamadaSiguiente() {
		Llamada siguienteLllamada = colallamadasEnEspera.poll();
		if (siguienteLllamada == null) {
			siguienteLllamada = colaLlamadasEntrantes.poll();
		}
		return siguienteLllamada;
	}
	
	/**
	 * Método que  agrega una llamada a la cola entrante
	 * @param llamada
	 * @return
	 */
	private boolean recibirLlamada(Llamada llamada) {
		return colaLlamadasEntrantes.offer(llamada);
	}

	/**
	 * Método que espera la finalziación del proceso
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void esperarFinalizacionLlamadas() throws InterruptedException, ExecutionException {

		indicadorEnOperacion.get();
	}
	
	/**
	 * 
	 * @return
	 */
	public Queue<Llamada> getColaLlamadasEntrantes() {
		return colaLlamadasEntrantes;
	}
	/**
	 * 
	 * @param colaLlamadasEntrantes
	 */
	public void setColaLlamadasEntrantes(Queue<Llamada> colaLlamadasEntrantes) {
		this.colaLlamadasEntrantes = colaLlamadasEntrantes;
	}
	/**
	 * 
	 * @return
	 */
	public Queue<Llamada> getColallamadasEnEspera() {
		return colallamadasEnEspera;
	}
	/**
	 * 
	 * @param colallamadasEnEspera
	 */
	public void setColallamadasEnEspera(Queue<Llamada> colallamadasEnEspera) {
		this.colallamadasEnEspera = colallamadasEnEspera;
	}
	/**
	 * 
	 * @return
	 */
	public Queue<Llamada> getColallamadasEnCurso() {
		return colallamadasEnCurso;
	}
	/**
	 * 
	 * @param colallamadasEnCurso
	 */
	public void setColallamadasEnCurso(Queue<Llamada> colallamadasEnCurso) {
		this.colallamadasEnCurso = colallamadasEnCurso;
	}
	/**
	 * 
	 * @return
	 */
	public Queue<Llamada> getColallamadasAtendidas() {
		return colallamadasAtendidas;
	}
	/**
	 * 
	 * @param colallamadasAtendidas
	 */
	public void setColallamadasAtendidas(Queue<Llamada> colallamadasAtendidas) {
		this.colallamadasAtendidas = colallamadasAtendidas;
	}
	/**
	 * 
	 * @return
	 */
	public int getCantidadOperadores() {
		return cantidadOperadores;
	}
	/**
	 * 
	 * @param cantidadOperadores
	 */
	public void setCantidadOperadores(int cantidadOperadores) {
		this.cantidadOperadores = cantidadOperadores;
	}
	/**
	 * 
	 * @return
	 */
	public int getCantidadSupervisores() {
		return cantidadSupervisores;
	}
	/**
	 * 
	 * @param cantidadSupervisores
	 */
	public void setCantidadSupervisores(int cantidadSupervisores) {
		this.cantidadSupervisores = cantidadSupervisores;
	}
	/**
	 * 
	 * @return
	 */
	public int getCantidadDirectores() {
		return cantidadDirectores;
	}
	/**
	 * 
	 * @param cantidadDirectores
	 */
	public void setCantidadDirectores(int cantidadDirectores) {
		this.cantidadDirectores = cantidadDirectores;
	}
	/**
	 * 
	 * @return
	 */
	public CompletableFuture<Llamada> getIndicadorEnOperacion() {
		return indicadorEnOperacion;
	}

}