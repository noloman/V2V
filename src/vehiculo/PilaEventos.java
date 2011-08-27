package vehiculo;

import java.util.*;

import org.jdom.Element;
import org.joda.time.DateTime;

/**
 * Esta va ser la clase que gestione todo lo relacionado con la cola de eventos
 * que el sistema debe mostrar al usuario.
 * Hereda de la clase Stack de Java y va a redefinir algunos de sus métodos.
 * @see Stack
 * @see Coche
 * @see Comprobaciones#anadirAlerta(String, PilaEventos, String, String, String, String, String, String)
 * @see Comprobaciones#anadirAlertaClimatica(String, PilaEventos, String, String, String, String)
 * @see Comprobaciones#anadirAlertaVeloMax(String, PilaEventos, String, String, String)
 * @author Manuel Lorenzo
 */

public class PilaEventos extends Stack 
{
	/**
	 * Constructur de la clase PilaEventos.
	 * Hereda de Stack
	 * @see Stack
	 */
	public PilaEventos(int tam) 
	{	
		Stack s = new Stack();
		s.setSize(tam);
	}
	
	/**
	 * @author Manuel Lorenzo
	 * Metodo que añade una alerta de cambio meteorologico a la pila de alertas. 
	 * Además de añadirla, comprueba las prioridades para determinar en qué lugar de la pila debe ir y mueve al resto de alertas en caso de que sea necesario. En el caso de que la pila esté llena, va a comprobar la distancia del resto de las alertas meteorológicas y a sustituirlas en caso de que la nueva alerta esté más lejos que alguna alerta que ya exista en la pila.
	 * @param prioridad es la prioridad que va tener una alerta climática
	 * @param pila es la pila donde se va a almacenar la alerta
	 * @param nomAlerta nombre de la alerta
	 * @param str_distancia_hasta_clima distancia aproximada hasta el lugar donde ha ocurrido un cambio de clima
	 * @param str_clima_a clima
	 * @param tiempo momento aproximado en el que ha ocurrido el cambio de clima
	 * @see Comprobaciones#CompruebaClima(Element,Element)
	 * @see Comprobaciones#anadirAlertaClimatica(String,PilaEventos,String,String,String,String)
	 * @see Comprobaciones#compruebaDuplicacionAlertaClimatica(String,String,String,String)
	 */
	
	public synchronized boolean addAlertaClimatica(String prioridad,PilaEventos pila,String nomAlerta,String str_distancia_hasta_clima,String str_clima_a,String tiempo) 
	{
		
		ArrayList alerta = new ArrayList();
		alerta.add(prioridad);
		alerta.add(nomAlerta);
		alerta.add(str_distancia_hasta_clima);
		//Es la distancia que nos separa de donde ha ocurrio el cambio de clima
		alerta.add(str_clima_a);
		alerta.add(tiempo);
		boolean added = false;
		
		if (pila.size() == Coche.TAM_PILA)
		{
			/*
			 * Pila llena. Hay que jugar con las prioridades para ver cual
			 * desalojamos. Una alerta añadida NUNCA puede desalojar a una
			 * de mayor prioridad. Si la prioridad es la misma, primero comprobamos las distancias,
			 * y si son iguales, se comprueba el que tenga la información más actual.
			 */

			// El iterador va a iterar sobre las alertas en la pila.
			ListIterator it_pila = this.listIterator();
			while (it_pila.hasNext()) 
			{
				ArrayList a = (ArrayList) it_pila.next();
				String prioridadEnPila = (String)a.get(0);
				if (prioridadEnPila.compareTo(prioridad) <= 0) 
				{
					// La alerta nueva es de mayor prioridad.
					it_pila.set(alerta);
					added = true;
					// Se cambia la anterior alerta por esta nueva.
				}
			}
		}

		else {
			/*
			 * Pila no llena. Añadimos la alerta en el lugar adecuado segun
			 * la prioridad. Hay que reordenar las alertas de la pila para
			 * que estén en el lugar adecuado.
			 */

			if (this.size() != 0) 
			{
				/*
				 * Si la pila NO está vacia. Si está vacia, simplemente
				 * insertamos la nueva alerta
				 */

				this.add(alerta);
				ListIterator it_pila = this.listIterator();
				int pos_nueva_alerta = -1;
				int cont = -1;
				while (it_pila.hasNext())
				{
					ArrayList a = (ArrayList)it_pila.next();
					cont++;
					String nombreAlerta = (String)a.get(1);
					if (nombreAlerta.equals(nomAlerta))
						pos_nueva_alerta = cont;
				}
				// Apunta a la alerta recien añadida
				
				int pos_otra_alerta = pos_nueva_alerta - 1;
				// Apunta a la alerta justo por debajo
				ArrayList alerta_nueva = (ArrayList) this.get(pos_nueva_alerta);
				ArrayList alerta_antigua = (ArrayList) this.get(pos_otra_alerta);
				String prioridad_alerta_nueva = (String) alerta_nueva.get(0);
				String prioridad_alerta_antigua = (String) alerta_antigua.get(0);
				if (prioridad_alerta_nueva.compareTo(prioridad_alerta_antigua) < 0) 
				{
					/*
					 * La prioridad de la alerta nueva es < que la prioridad
					 * de la alerta de abajo.
					 * Lo dejamos asi.
					 */
				}
				else if (prioridad_alerta_nueva.compareTo(prioridad_alerta_antigua)==0)
				{
					/*
					 * Ambas alertas son de la misma prioridad y por tanto del mismo tipo.
					 * Comprobamos las distancias para colocarlas en el sitio adecuado.
					 */
					
					String dist_alerta_nueva = (String)alerta_nueva.get(2);
					String dist_alerta_antigua = (String)alerta_antigua.get(2);
					if (dist_alerta_nueva.compareTo(dist_alerta_antigua) > 0)
					{
						/*
						 * La alerta nueva es una de velocidad propia, y por tanto va ENCIMA de la antigua.
						 * La nueva alerta es la más actual (más arriba).
						 * La vieja es la de abajo.
						 * Intercambiamos para que la alerta nueva tenga la posicion 0.
						 */
						
						ArrayList temp = new ArrayList();
						temp = alerta_nueva;
						this.set(pos_nueva_alerta, alerta_antigua);
						this.set(pos_otra_alerta, temp);
						added = true;
					}
					
					else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) < 0)
						
					{
						/*
						 * Ninguna distancia es 0, lo que hay que ver ahora es cuál es mayor o menor.
						 * La distancia de la alerta nueva es menor, entonces la nueva es MAS prioritaria y se deja asi.
						 * Dejamos asi.
						 */
						
					}
					else if (dist_alerta_nueva.compareTo(dist_alerta_antigua)==0)
					{
						/*
						 * Ambas alertas son la misma. Comprobamos las marcas de tiempo.
						 */
						DateTime tiempo_alerta_pila = new DateTime(alerta_antigua.get(4));
						DateTime tiempo_alerta_nueva_dt = new DateTime(alerta_nueva.get(4));
						if (tiempo_alerta_nueva_dt.isAfter(tiempo_alerta_pila))
						{
							/*
							 * Si el la marca de tiempo de la alerta nueva es más actual que la de la alerta
							 * que hay en la pila, se cambia el orden de las alertas en la pila.
							 */
							ArrayList temp = new ArrayList();
							temp = alerta_nueva;
							this.set(pos_nueva_alerta, alerta_antigua);
							this.set(pos_otra_alerta, temp);
							added = true;
						}
					}
				}
			}

			else 
			{
				/*
				 * Pila vacia. Insertamos simplemente la nueva alerta.
				 */

				this.add(alerta);
				added = true;
			}
		}
		return added;
	}
	
	/**
	 * @author Manuel Lorenzo
	 * Metodo que añade una alerta de velocidad maxima a la pila de alertas del sistema.
	 * Nos va a servir para comprobar si es una alerta por velocidad propia o no, ya que si es una alerta por velocidad maxima captada por nuestro vehiculo, la distancia sera "".
	 * Además de añadirla, comprueba las prioridades para determinar en qué lugar de la pila debe ir y mueve al resto de alertas en caso de que sea necesario. En el caso de que la pila esté llena, va a comprobar la distancia del resto de las alertas por velocidad maxima y a sustituirlas en caso de que la nueva alerta esté más lejos que alguna alerta que ya exista en la pila.
	 * @param prioridad prioridad que se le asigna a las alertas por velocidad maxima en la pila de alertas
	 * @param nombreAlertaFinal nombre de la alerta por velocidad maxima
	 * @param str_vmax velocidad maxima
	 * @param distancia_aprox distancia aproximada a la cual se encuentra el punto de cambio de velocidad maxima
	 * @param dist_aprox: es la distancia a la cual se encontraba el vehiculo que nos envio la alerta de velocidad maxima.
	 * @see Comprobaciones#CompruebaVelocidadMax(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#compruebaDuplicacionAlertaVeloMax(PilaEventos, String)
	 * @see PilaEventos#addAlertaVeloMax(String, String, String, String)
	 */
	
	public synchronized boolean addAlertaVeloMax(String prioridad,String nombreAlertaFinal,String str_vmax,String distancia_aprox,String hora) 
	{
		
		ArrayList alerta = new ArrayList();
		alerta.add(prioridad);
		alerta.add(nombreAlertaFinal);
		alerta.add(str_vmax);
		alerta.add(distancia_aprox);
		alerta.add(hora);
		boolean added = false;
		
		if (Coche.pila.size() == Coche.TAM_PILA)
		{
			/*
			 * Pila llena. Hay que jugar con las prioridades para ver cual
			 * desalojamos. Una alerta añadida NUNCA puede desalojar a una
			 * de mayor prioridad. Si la prioridad es la misma, damos
			 * prioridad a esta nueva alerta.
			 */

			// El iterador va a iterar sobre las alertas en la pila.
			ListIterator it_pila = this.listIterator();
			while (it_pila.hasNext()) 
			{
				ArrayList a = (ArrayList) it_pila.next();
				String prioridadEnPila = (String)a.get(0);
				if (prioridadEnPila.compareTo(prioridad) <= 0) 
				{
					// La alerta nueva es de mayor prioridad.
					it_pila.set(alerta);
					added = true;
					// Se cambia la anterior alerta por esta nueva.
				}
			}
		}

		else 
		{
			/*
			 * Pila no llena. Añadimos la alerta en el lugar adecuado segun
			 * la prioridad. Hay que reordenar las alertas de la pila para
			 * que estén en el lugar adecuado.
			 */

			if (this.size() != 0) 
			{
				/*
				 * Si la pila NO está vacia. Si está vacia, simplemente
				 * insertamos la nueva alerta
				 */

				this.add(alerta);
				ListIterator it_pila = this.listIterator();
				int pos_nueva_alerta = -1;
				int cont = -1;
				while (it_pila.hasNext())
				{
					ArrayList a = (ArrayList)it_pila.next();
					cont++;
					String nombreAlerta = (String)a.get(1);
					if (nombreAlerta.equals(nombreAlertaFinal))
						pos_nueva_alerta = cont;
				}
				// Apunta a la alerta recien añadida
				int pos_otra_alerta = pos_nueva_alerta - 1;
				// Apunta a la alerta justo por debajo
				ArrayList alerta_nueva = (ArrayList)this.get(pos_nueva_alerta);
				ArrayList alerta_antigua = (ArrayList)this.get(pos_otra_alerta);
				String prioridad_alerta_nueva = (String) alerta_nueva.get(0);
				String prioridad_alerta_antigua = (String) alerta_antigua.get(0);
				if (prioridad_alerta_nueva.compareTo(prioridad_alerta_antigua) < 0) 
				{
					/*
					 * La prioridad de la alerta nueva es < que la prioridad
					 * de la alerta de abajo .
					 * Hay que intercambiar posiciones pues.
					 */

					ArrayList temp = new ArrayList();
					temp = alerta_nueva;
					this.set(pos_nueva_alerta, alerta_antigua);
					this.set(pos_otra_alerta, temp);
					added = true;
				}
				else if (prioridad_alerta_nueva.compareTo(prioridad_alerta_antigua)==0)
				{
					/*
					 * Si las prioridades son iguales, ambas son alertas de velocidad maxima.
					 * Ahora hay que comprobar las distancias y colocarlas en la pila segun.
					 * Si la distancia es 0, entonces es alerta propia y va arriba del todo.
					 * Si no es 0, se comparan y se pone la más cercana arriba.
					 */
					
					String dist_alerta_nueva = (String)alerta_nueva.get(3);
					String dist_alerta_antigua = (String)alerta_antigua.get(3);
					
					if (dist_alerta_nueva.equals(""))
					{
						/*
						 * La alerta nueva es una de velocidad propia, y por tanto va ENCIMA de la antigua.
						 * La nueva alerta es la más actual (más arriba).
						 * La vieja es la de abajo.
						 * Intercambiamos para que la alerta nueva tenga la posicion 0.
						 */
						
						ArrayList temp = new ArrayList();
						temp = alerta_nueva;
						this.set(pos_nueva_alerta, alerta_antigua);
						this.set(pos_otra_alerta, temp);
						added = true;
					}
					
					else if (dist_alerta_antigua.equals(""))
					{
						/*
						 * La de abajo es la alerta de velocidad normal, y va a estar arriba.
						 */
					}
					
					else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) > 0)
					{
						/*
						 * Ninguna distancia es 0, lo que hay que ver ahora es cuál es mayor o menor.
						 * Si la distancia de la alerta nueva es mayor, entonces la nueva es MENOS prioritaria y hay que intercambiar.
						 */
						
					}
					else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) < 0)
					{
						/*
						 * Ninguna distancia es 0, lo que hay que ver ahora es cuál es mayor o menor.
						 * La distancia de la alerta nueva es menor, entonces la nueva es MAS prioritaria y se deja asi.
						 * Intercambiamos.
						 */
						ArrayList temp = new ArrayList();
						temp = alerta_nueva;
						this.set(pos_nueva_alerta, alerta_antigua);
						this.set(pos_otra_alerta, temp);
						added = true;
					}
					else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) == 0)
					{
						DateTime tiempo_alerta_pila = new DateTime(alerta_antigua.get(4));
						DateTime tiempo_alerta_nueva_dt = new DateTime(alerta_nueva.get(4));
						if (tiempo_alerta_nueva_dt.isAfter(tiempo_alerta_pila))
						{
							/*
							 * Si el la marca de tiempo de la alerta nueva es más actual que la de la alerta
							 * que hay en la pila, se cambia el orden de las alertas en la pila.
							 */
							ArrayList temp = new ArrayList();
							temp = alerta_nueva;
							this.set(pos_nueva_alerta, alerta_antigua);
							this.set(pos_otra_alerta, temp);
							added = true;
						}
					}
				}
			}

			else 
			{
				/*
				 * Pila vacia. Insertamos simplemente la nueva alerta.
				 */

				this.add(alerta);
				added = true;
			}
		}
		return added;
	}
	
	/**
	 * @author Manuel Lorenzo
	 * Este metodo va a ser el que se use para añadir a la pila una alerta genérica, es decir, cualquier tipo de alerta que no sea ni de velocidad maxima ni climatológica.
	 * Al añadirse la alerta se va a llamar al metodo buscaYSustituyeAlerta para comprobar si ya existe una alerta de ese tipo y si hay que crear una nueva o simplemente actualizar la existente.
	 * Tras esto, se comprueba el tamaño de la pila para ver qué hay que hacer con ella, y en caso de que haya alertas en la pila, se añade la nueva alerta y se reordenan las alertas segun la prioridad si hace falta.
	 * @param nombreAlerta nombre que se le va a dar a la alerta
	 * @param velocidad_vehiculo_minimo velocidad que va a llevar el vehiculo que ocasiona la alerta; en caso de que haya varios que puedan generar una alerta, es el que se encuentra más cercano a nosotros
	 * @param prioridad prioridad del tipo de alerta. Viene ya especificada en el método correspondiente de la clase {@link Comprobaciones}
	 * @param mi_dist distancia recorrida por nuestro vehiculo
	 * @param distancia_vehiculo_minimo distancia recorrida por el vehiculo que ocasiona la alerta
	 * @param nombreVehiculo nombre del vehiculo que ocasiona la alerta
	 * @param tiempo marca de tiempo del vehiculo que ocasiona la alerta. Nos va a servir para, en caso de igualdad de prioridades, igualdad de distancias entre nuestro vehiculo y el que ocasiona la alerta, a la hora de añadir una nueva alerta se tomaría el vehiculo con la marca de tiempo más actual.
	 * @see Comprobaciones#CompruebaVehiculoEmergencias(Element,Element)
	 * @see Comprobaciones#CompruebaFrenada(Element,Element)
	 * @see Comprobaciones#CompruebaEstado(Element,Element)
	 * @see Comprobaciones#CompruebaVehiculoEmergencias(Element,Element)
	 * @see Comprobaciones#anadirAlerta(String,PilaEventos,String,String,String,String,String,String)
	 */

	public synchronized boolean add(String nombreAlerta,Object velocidad_vehiculo_minimo,String distancia_vehiculo_minimo,String prioridad,String mi_dist,String nombreVehiculo,String tiempo) {
		ArrayList alerta = new ArrayList();
		alerta.add(nombreAlerta);
		alerta.add(velocidad_vehiculo_minimo);
		alerta.add(distancia_vehiculo_minimo);
		alerta.add(prioridad);
		alerta.add(mi_dist);
		alerta.add(nombreVehiculo);
		alerta.add(tiempo);
		boolean added = false;
		boolean sustituido = false;
		sustituido = buscaYSustituyeAlerta(Coche.pila, alerta);
		if (sustituido == false) 
		{
			if (this.size() == 4) 
			{
				/*
				 * Pila llena. Hay que jugar con las prioridades para ver cual
				 * desalojamos. Una alerta añadida NUNCA puede desalojar a una
				 * de mayor prioridad. Si la prioridad es la misma, damos
				 * prioridad a esta nueva alerta.
				 */

				// El iterador va a iterar sobre las alertas en la pila.
				ListIterator it_pila = this.listIterator();
				while (it_pila.hasNext()) 
				{
					ArrayList a = (ArrayList) it_pila.next();
					String prioridadEnPila = (String)a.get(3);
					if (prioridadEnPila.compareTo(prioridad) <= 0) {
						// La alerta nueva es de mayor prioridad.
						it_pila.set(alerta);
						added = true;
						// Se cambia la anterior alerta por esta nueva.
					}

				}
			}

			else 
			{
				/*
				 * Pila no llena. Añadimos la alerta en el lugar adecuado segun
				 * la prioridad. Hay que reordenar las alertas de la pila para
				 * que estén en el lugar adecuado.
				 */

				if (this.size() != 0) 
				{
					/*
					 * Si la pila NO está vacia. Si está vacia, simplemente
					 * insertamos la nueva alerta
					 */

					this.add(alerta);
					ListIterator it_pila = this.listIterator();
					int pos_nueva_alerta = -1;
					int cont = -1;
					while (it_pila.hasNext())
					{
						ArrayList a = (ArrayList)it_pila.next();
						cont++;
						String nomAlerta =(String)a.get(0);
						if (nombreAlerta.equals(nomAlerta))
							pos_nueva_alerta = cont;
					}
					// Apunta a la alerta recien añadida
					int pos_otra_alerta = pos_nueva_alerta - 1;
					// Apunta a la alerta justo por debajo
					ArrayList alerta_nueva = (ArrayList) this.get(pos_nueva_alerta);
					ArrayList alerta_otra = (ArrayList) this.get(pos_otra_alerta);
					String prioridad_alerta_nueva = (String) alerta_nueva.get(3);
					String prioridad_alerta_otra = (String) alerta_otra.get(3);
					if (prioridad_alerta_nueva.compareTo(prioridad_alerta_otra) < 0) 
					{
						/*
						 * La prioridad de la alerta nueva es < que la prioridad
						 * de la alerta de abajo Hay que intercambiar posiciones
						 * pues.
						 */

						ArrayList temp = new ArrayList();
						temp = alerta_nueva;
						this.set(pos_nueva_alerta, alerta_otra);
						this.set(pos_otra_alerta, temp);
						added = true;
					}
					else
					{
						DateTime tiempo_alerta_pila = new DateTime(alerta_otra.get(6));
						DateTime tiempo_alerta_nueva_dt = new DateTime(alerta_nueva.get(6));
						if (tiempo_alerta_nueva_dt.isAfter(tiempo_alerta_pila))
						{
							/*
							 * Si el la marca de tiempo de la alerta nueva es más actual que la de la alerta
							 * que hay en la pila, se cambia el orden de las alertas en la pila.
							 */
							ArrayList temp = new ArrayList();
							temp = alerta_nueva;
							this.set(pos_nueva_alerta, alerta_otra);
							this.set(pos_otra_alerta, temp);
							added = true;
						}
					}
				}
				else 
				{
					/*
					 * Pila vacia. Insertamos simplemente la nueva alerta.
					 */
					this.add(alerta);
					added = true;
				}
			}
		}
		return added;
	}
	
	/**
	 * Metodo para eliminar alertas de la pila.
	 * @param nombreAlerta nombre de la alerta a eliminar.
	 */
	
	public synchronized boolean remove(String nombreAlerta)
	{
		
		boolean rem = false;
		String nomAlerta = null;
		ArrayList alerta = new ArrayList();
		ListIterator it = this.listIterator();
		alerta.add(nombreAlerta);
		int pos_alerta = -1;
		int cont = -1;
		while (it.hasNext())
		{
			ArrayList a = (ArrayList)it.next();
			cont++;
			if (nombreAlerta.contains("AlertaClima") || (nombreAlerta.contains("AlertaVelocidadMax")))
				nomAlerta = (String)a.get(1);
			else
				nomAlerta = (String)a.get(0);
			if (nombreAlerta.equals(nomAlerta))
				pos_alerta = cont;
		}
		if (pos_alerta != -1) 
		{
			this.remove(pos_alerta);
			rem = true;
		}
		return rem;
	}
	 
	 /**
	 * Es el metodo para comprobar si ya existe en la pila una determinada alerta genérica.
	 * En caso de que ya exista, se comprueba si la información que contiene la nueva alerta es más actual, en cuyo caso se sustituye la antigua que está en la pila por esta.
	 * @see PilaEventos#add(String,Object,String,String,String,String,String)
	 * @see Comprobaciones#anadirAlerta(String, PilaEventos, String, String, String, String, String, String)
	 * @author Manuel Lorenzo
	 * @param pila pila de alertas del sistema
	 * @param alerta alerta genérica que se le pasa al metodo para la comprobacion
	 * @return boolean
	 */
	public boolean buscaYSustituyeAlerta(PilaEventos pila, ArrayList alerta) 
	{
		//Queremos ver si en la pila, ya esta la alerta que hay en ArrayList alerta
		boolean sustituido = false;

		/*
		 * Este booleano va a servir para que si devuelve TRUE, significa que ha
		 * modificado una alerta y no hay que añadir. En caso contrario, si
		 * devuelve FALSE, significa que no ha modificado y hay que añadir nueva
		 * alerta.
		 */

		String nombreAlerta = (String) alerta.get(0);
		String velocidad_vehiculo = (String) alerta.get(1);
		// Si este valor es 0, es porque se trata de una alerta de estado de
		// vehiculo detenido.
		String prioridad = (String) alerta.get(3);
		String dist_veh = (String) alerta.get(2);
		String mi_dist = (String) alerta.get(4);
		String nombre_vehiculo = (String) alerta.get(5);
		String tiempo_vehiculo = (String) alerta.get(6);
		ListIterator it = pila.listIterator();
		// Itera sobre la pila
		while (it.hasNext()) 
		{
			ArrayList a = (ArrayList) it.next();
			String nombreVehiculo = (String) a.get(5);
			// Es el nombre del vehiculo que provoca la alerta.
			if (nombreVehiculo.equals(nombre_vehiculo))
			{
				/*
				 * Comprueba si el nombre del vehiculo en la alerta en pila es
				 * igual al mismo de la alerta en el ArrayList
				 */

				if (nombreAlerta.compareTo((String)a.get(0)) > 0) 
				{
					/*
					 * Si el nombre del vehiculo pasado por parametros esta ya
					 * en alguna alerta dentro de la pila y la alerta es mas
					 * nuevo que la que ya esta en pila (esto lo sabemos
					 * haciendo un compareTo para ver si el numero de la alerta
					 * que hay en el arraylist pasado por parametros es mayor
					 * que el que hay en pila) entonces actualizamos esa alerta
					 * con toda la información nueva que nos dan.
					 */

					String tiempo_pila = (String) a.get(6);
					DateTime tiempo_pila_dt = new DateTime(tiempo_pila);
					DateTime tiempo_array_dt = new DateTime(tiempo_vehiculo);
					if (tiempo_array_dt.isAfter(tiempo_pila_dt))
					{
						/*
						 * Si el la información del vehiculo en la alerta pasada
						 * por parametro es más actual, sustituimos la alerta en
						 * la pila.
						 */
						a.set(1, velocidad_vehiculo);
						a.set(2, dist_veh);
						a.set(3, prioridad);
						a.set(4, mi_dist);
						a.set(5, nombre_vehiculo);
						a.set(6, tiempo_vehiculo);
						/*
						 * No hace falta cambiar ni prioridad, ni
						 * nombre_vehiculo
						 */
						it.set(a);
						sustituido = true;
					}
				}
			}

			else

			{
				/*
				 * Posibilita que se cree una nueva alerta
				 */
				sustituido = false;
			}

			// Si devuelve false, significa que HAY que añadir la alerta
		}
		return sustituido;
	}
	
	/**
	 * Método para la ordenación de las alertas en la pila.
	 * Está pensado para ser llamado al final del método {@link Comprobaciones#Comprobaciones()} para que ordene las
	 * alertas en la pila de la forma adecuada.
	 * Aunque ya al añadir una alerta se cuida de ponerle en el lugar adecuado según la prioridad, se ha creado éste método
	 * debido a que en el caso de que el fichero XML que contiene la información de un vehículo sea de un tamaño muy grande, es posible
	 * que se tarde en hacer cada una de las comprobaciones, y debido a un elevado flujo de información podrian no ordenarse de la forma
	 * precisa las alertas, así que se llama al método éste en ultimo lugar para asegurarnos de la correcta ordenación.
	 * @see Comprobaciones#Comprobaciones(String)
	 * @see Comprobaciones#CompruebaClima(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#CompruebaEstado(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#CompruebaFrenada(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#CompruebaVehiculoAdelantando(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#CompruebaVehiculoEmergencias(org.jdom.Element, org.jdom.Element)
	 * @see Comprobaciones#CompruebaVelocidadMax(org.jdom.Element, org.jdom.Element)
	 * @param pila
	 */
	public void ordenaPila(PilaEventos pila)
	{
		ListIterator it = pila.listIterator();
		int numElem = pila.size();
		int pos_fija = -1;
		int pos_it = -1;
		boolean fin = false;
		boolean cambiado = false;
		ArrayList alerta_2 = new ArrayList();
		while (it.hasNext())
		{
			fin = false;
			pos_fija++;
			pos_it = pos_fija;
			ArrayList alerta_1 = (ArrayList)it.next();
			String prioridad_1 = (String)alerta_1.get(3);
			if (prioridad_1.length()>1)
			{
				/*
				 * Es una alerta genérica y prioridad_1 tiene el valor del clima.
				 */
				prioridad_1 = (String)alerta_1.get(0);
			}
			while (!fin)
			{
				pos_it++;
			/*
			 * Las alertas por velocidad y clima tienen la prioridad en la posición 0 del ArrayList,
			 * mientras que las genéricas lo tienen en la posición 0.
			 * Lo que se va a hacer es comprobar si lo que devuelve alerta_1.get(3) es > 1, porque
			 * entonces significa que esta alerta es de clima
			 */
			int pos_alerta_1 = pos_fija;
			int pos_alerta_2 = pos_it;
			int control = pos_it;
			// Apunta a la alerta justo por debajo
			if (control < numElem)
			{
				alerta_2 = (ArrayList)this.get(pos_alerta_2);
			}
			else
				fin = true;
			if (!fin)
			{
			ArrayList a = (ArrayList)this.get(pos_alerta_1);
			prioridad_1 = (String)a.get(3);
			if (prioridad_1.length()>1)
			{
				/*
				* Es una alerta genérica y prioridad_1 tiene el valor del clima.
				*/
				prioridad_1 = (String)alerta_1.get(0);
			}
			String prioridad_2 = (String)alerta_2.get(3);
			if (prioridad_2.length()>1)
			{
				/*
				 * Es una alerta genérica y prioridad_1 tiene el valor del clima.
				 */
				prioridad_2 = (String)alerta_2.get(0);
			}
			if (prioridad_1.compareTo(prioridad_2) < 0)
			{
				/*
				 * La prioridad de la alerta nueva es < que la prioridad
				 * de la alerta de abajo. Hay que intercambiar posiciones,
				 * pues.
				 */

				ArrayList temp = new ArrayList();
				temp = (ArrayList)this.get(pos_alerta_1);
				this.set(pos_alerta_1,(Object)alerta_2);
				this.set(pos_alerta_2,temp);
				cambiado = true;
			}
			else if (prioridad_1.compareTo(prioridad_2) > 0)
			{
				/*
				 * No se hace nada; la prioridad de la alerta de arriba es mayor
				 * que la de la alerta de debajo.
				 */
			}
			else
			{
				/*
				 * Aqui solo va a entrar si las prioridades de dos alertas son las mismas, lo cual quiere decir
				 * que son el mismo tipo de alerta.
				 * En este apartado hay que hacer distinciones.
				 * Ya que la distincio entre las alertas genericas y las de clima, cuando son
				 * del mismo tipo, es que las genéricas se subordenan por marca de tiempo y las de velocidad 
				 * y clima por distancia, hay que comprobar el tipo de alerta en caso de que ambas alertas
				 * sean la misma, y actual por tanto en consecuencia.
				 */
				String tipo_1 = (String)alerta_1.get(1);
				String tipo_2 = (String)alerta_2.get(1);
				/*
				 * Si el contenido de ambas variables es
				 */
				if (tipo_1.contains(("AlertaClima")) || tipo_1.contains("AlertaVelMax"))
				{
					/*
					 * Si es una alerta de velocidad o clima, se subordenan por distancia, y si no, por marca de tiempo.
					 */
					if (prioridad_1.compareTo(prioridad_2)==0)
					{
						/*
						 * Si las prioridades son iguales, ambas son alertas de velocidad maxima.
						 * Ahora hay que comprobar las distancias y colocarlas en la pila segun.
						 * Si la distancia es 0, entonces es alerta propia y va arriba del todo.
						 * Si no es 0, se comparan y se pone la más cercana arriba.
						 */
						
						String dist_alerta_nueva = (String)alerta_1.get(3);
						String dist_alerta_antigua = (String)alerta_1.get(3);
						
						if (dist_alerta_nueva.equals(""))
						{
							/*
							 * La alerta nueva es una de velocidad propia, y por tanto va ENCIMA de la antigua.
							 * La nueva alerta es la más actual (más arriba).
							 * La vieja es la de abajo.
							 * Intercambiamos para que la alerta nueva tenga la posicion 0.
							 */
							
							ArrayList temp = new ArrayList();
							temp = (ArrayList)this.get(pos_alerta_1);
							this.set(pos_alerta_1, alerta_2);
							this.set(pos_alerta_2, temp);
							cambiado = true;
						}
						else if (dist_alerta_antigua.equals(""))
						{
							/*
							 * La de abajo es la alerta de velocidad normal, y va a estar arriba.
							 */
						}
						else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) > 0)
						{
							/*
							 * Ninguna distancia es 0, lo que hay que ver ahora es cuál es mayor o menor.
							 * Si la distancia de la alerta nueva es mayor, entonces la nueva es MENOS prioritaria y hay que intercambiar.
							 */
							
						}
						else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) < 0)
						{
							/*
							 * Ninguna distancia es 0, lo que hay que ver ahora es cuál es mayor o menor.
							 * La distancia de la alerta nueva es menor, entonces la nueva es MAS prioritaria y se deja asi.
							 * Intercambiamos.
							 */
							ArrayList temp = new ArrayList();
							temp = (ArrayList)this.get(pos_alerta_1);
							this.set(pos_alerta_1, alerta_2);
							this.set(pos_alerta_2, temp);
							cambiado = true;
						}
						else if (dist_alerta_nueva.compareTo(dist_alerta_antigua) == 0)
						{
							DateTime tiempo_alerta_1 = new DateTime(alerta_1.get(4));
							DateTime tiempo_alerta_2 = new DateTime(alerta_2.get(4));
							if (tiempo_alerta_2.isAfter(tiempo_alerta_1))
							{
								/*
								 * Si el la marca de tiempo de la alerta nueva es más actual que la de la alerta
								 * que hay en la pila, se cambia el orden de las alertas en la pila.
								 */
								ArrayList temp = new ArrayList();
								//temp = alerta_1;
								temp = (ArrayList)this.get(pos_alerta_1);
								this.set(pos_alerta_1, alerta_2);
								this.set(pos_alerta_2, temp);
								cambiado = true;
							}
						}
					}
				}
				else
				{
					/*
					 * Si se trata de una alerta genérica puesto que en las genericas, el get(1) no es el nombre
					 * y por tanto nunca coincide ni con AlertaClima ni con AlertaVelMax
					 * Se subordenan por marca de tiempo entonces.
					 */
					DateTime tiempo_alerta_1 = new DateTime(alerta_1.get(6));
					DateTime tiempo_alerta_2 = new DateTime(alerta_2.get(6));
					if (tiempo_alerta_2.isAfter(tiempo_alerta_1))
					{
						/*
						 * Si el la marca de tiempo de la alerta nueva es más actual que la de la alerta
						 * que hay en la pila, se cambia el orden de las alertas en la pila.
						 */
						ArrayList temp = new ArrayList();
						temp = (ArrayList)this.get(pos_alerta_1);
						this.set(pos_alerta_1, alerta_2);
						this.set(pos_alerta_2, temp);
					}
				}
			}
			}
		}
		}
	}
	public void muestraAlertas(PilaEventos pila)
	{
		String tipo_clima = null;
		String velocidad_vehiculo_minimo = null;
		String distancia_vehiculo_minimo = null;
		String mi_dist = null;
		String nombreVehiculo = null;
		String tiempo = null;
		String prioridad = null;
		String vmax = null;
		String distancia = null;
		String hora = null;
		ListIterator it = pila.listIterator();
		while (it.hasNext())
		{
			ArrayList alerta = (ArrayList)it.next();
			String nombre = (String)alerta.get(0);
			if (nombre.length()<2)
			{
				/*
				 * Es una alerta climática, y el nombre está en la posicion 1 del arraylist
				 */
				nombre = (String)alerta.get(1);
			}
			if (nombre.contains(("AlertaVelMax")))
			{
				prioridad = (String)alerta.get(0); 
				vmax = (String)alerta.get(2);
				distancia = (String)alerta.get(3);
				hora = (String)alerta.get(4);
				
				System.out.println("Alerta por velocidad maxima");
				System.out.println("=================");
				System.out.println("Nombre de la alerta: "+nombre);
				System.out.println("-----------------");
				System.out.println("Prioridad: "+prioridad);
				System.out.println("-----------------");
				System.out.println("Velocidad maxima: "+vmax);
				System.out.println("-----------------");
				System.out.println("Distancia: "+distancia);
				System.out.println("-----------------");
				System.out.println("Marca de tiempo: "+hora);
				System.out.println("");
				System.out.println("");
				System.out.println("");
				it.remove();
			}
			else if (nombre.contains(("AlertaClima")))
			{
				prioridad = (String)alerta.get(0); 
				distancia = (String)alerta.get(2);
				tipo_clima = (String)alerta.get(3);
				hora = (String)alerta.get(4);
				
				System.out.println("Alerta Climatica");
				System.out.println("=================");
				
				System.out.println("Nombre de la alerta: "+nombre);
				System.out.println("-----------------");
				System.out.println("Prioridad: "+prioridad);
				System.out.println("-----------------");
				System.out.println("Distancia hasta el cambio de clima: "+distancia);
				System.out.println("-----------------");
				System.out.println("Tipo de clima: "+tipo_clima);
				System.out.println("-----------------");
				System.out.println("Marca de tiempo: "+hora);
				System.out.println("");
				System.out.println("");
				System.out.println("");
				it.remove();
			}
			else
			{
				velocidad_vehiculo_minimo = (String)alerta.get(1);
				distancia_vehiculo_minimo = (String)alerta.get(2);
				prioridad = (String)alerta.get(3);
				mi_dist = (String)alerta.get(4);
				nombreVehiculo = (String)alerta.get(5);
				tiempo = (String)alerta.get(6);
				
				if (nombre.contains(("AlertaVehiculoEmergencias")))
				{
					System.out.println("Alerta por adelantamiento de vehiculo de emergencias");
					System.out.println("====================================================");
				
					System.out.println("Nombre de la alerta: "+nombre);
					System.out.println("-----------------");
					System.out.println("Velocidad del vehiculo ajeno: "+velocidad_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Distancia del vehiculo ajeno: "+distancia_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Prioridad: "+prioridad);
					System.out.println("-----------------");
					System.out.println("Distancia recorrida por el vehiculo de la pila: "+mi_dist);
					System.out.println("-----------------");
					System.out.println("Nombre del vehiculo ajeno: "+nombreVehiculo);
					System.out.println("-----------------");
					System.out.println("Marca de tiempo: "+hora);
					System.out.println("");
					System.out.println("");
					System.out.println("");
					it.remove();
				}
				if (nombre.contains(("AlertaAdelantando")))
				{
					System.out.println("Alerta por adelantamiento de vehiculo");
					System.out.println("====================================================");
					System.out.println("Nombre de la alerta: "+nombre);
					System.out.println("-----------------");
					System.out.println("Velocidad del vehiculo ajeno: "+velocidad_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Distancia del vehiculo ajeno: "+distancia_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Prioridad: "+prioridad);
					System.out.println("-----------------");
					System.out.println("Distancia recorrida por el vehiculo de la pila: "+mi_dist);
					System.out.println("-----------------");
					System.out.println("Nombre del vehiculo ajeno: "+nombreVehiculo);
					System.out.println("-----------------");
					System.out.println("");
					System.out.println("");
					System.out.println("");
					it.remove();
				}
				if (nombre.contains(("AlertaEstado")))
				{
					System.out.println("Alerta por vehiculo detenido");
					System.out.println("====================================================");
					System.out.println("Nombre de la alerta: "+nombre);
					System.out.println("-----------------");
					System.out.println("Velocidad del vehiculo ajeno: "+velocidad_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Distancia del vehiculo ajeno: "+distancia_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Prioridad: "+prioridad);
					System.out.println("-----------------");
					System.out.println("Distancia recorrida por el vehiculo de la pila: "+mi_dist);
					System.out.println("-----------------");
					System.out.println("Nombre del vehiculo ajeno: "+nombreVehiculo);
					System.out.println("-----------------");
					System.out.println("Marca de tiempo: "+hora);
					System.out.println("");
					System.out.println("");
					System.out.println("");
					it.remove();
				}
				if (nombre.contains(("AlertaAtasco")))
				{
					System.out.println("Alerta por frenada brusca");
					System.out.println("====================================================");
					System.out.println("Nombre de la alerta: "+nombre);
					System.out.println("-----------------");
					System.out.println("Velocidad del vehiculo ajeno: "+velocidad_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Distancia del vehiculo ajeno: "+distancia_vehiculo_minimo);
					System.out.println("-----------------");
					System.out.println("Prioridad: "+prioridad);
					System.out.println("-----------------");
					System.out.println("Distancia recorrida por el vehiculo de la pila: "+mi_dist);
					System.out.println("-----------------");
					System.out.println("Nombre del vehiculo ajeno: "+nombreVehiculo);
					System.out.println("-----------------");
					System.out.println("Marca de tiempo: "+hora);
					System.out.println("");
					System.out.println("");
					System.out.println("");
					it.remove();
				}
			}
		}
	}
}