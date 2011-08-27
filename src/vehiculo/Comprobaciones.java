package vehiculo;

import java.awt.Component;
import java.util.*;

import javax.rmi.CORBA.Tie;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.*;
import org.joda.time.*;
import xml.manejaXML;

/**
 * Clase que gestiona todas las posibles comprobaciones que se pueden hacer a un determiando vehiculo.
 * Consta de un metodo {@link Comprobaciones(String)} que será el encargado de llamar a cada uno de los tipos especificos de comprobacion mediante sus correspondientes métodos.
 * @see Comprobaciones#CompruebaVehiculoEmergencias(Element, Element)
 * @see Comprobaciones#CompruebaVehiculoAdelantando(Element, Element)
 * @see Comprobaciones#CompruebaEstado(Element, Element)
 * @see Comprobaciones#CompruebaFrenada(Element, Element)
 * @see Comprobaciones#CompruebaClima(Element, Element)
 * @see Comprobaciones#CompruebaVelocidadMax(Element, Element)
 * @author Manuel Lorenzo
 */
public class Comprobaciones 
{
	public static Document doc;
	/**
	 * Función será la encargada de hacer todas las comprobaciones, llamando a cada uno de los métodos que comprobarán especificamente cada una de las situaciones. 
	 * Se ha diseñado para que el metodo {@link Comprobaciones#Comprobaciones(String)} sea llamado cada vez que se recibe un fichero XML y por tanto se actualiza o reciba nueva información.
	 * @param fileNameMiVehiculo nombre del fichero (XML) sobre el que se van a hacer las comprobaciones. Es el fichero que contiene la informacion conocida por el vehiculo, es decir, vehiculo.xml
	 * @see Comprobaciones#CompruebaVehiculoEmergencias(Element, Element)
	 * @see Comprobaciones#CompruebaVehiculoAdelantando(Element, Element)
	 * @see Comprobaciones#CompruebaEstado(Element, Element)
	 * @see Comprobaciones#CompruebaFrenada(Element, Element)
	 * @see Comprobaciones#CompruebaClima(Element, Element)
	 * @see Comprobaciones#CompruebaVelocidadMax(Element, Element)
	 */
	public static void Comprobaciones(String fileNameMiVehiculo) 
	{
		try 
		{
			SAXBuilder builder = new SAXBuilder(false);
			doc = builder.build(fileNameMiVehiculo);
			Element raiz = doc.getRootElement();
			Element info_propia = raiz.getChild("informacion_propia");
			Element informacion_ajena_recibida = raiz.getChild("informacion_ajena");

			//Coche.pila.add("AlertaAdelantando1","120","1.5","1","2.0","Vehiculo3","2009-05-18T11:10:12.585+02:00");
			CompruebaVehiculoAdelantando(informacion_ajena_recibida,info_propia);
			
			//Coche.pila.add("AlertaEstado1","0","3.0","2","2.0","Vehiculo3","2009-05-19T11:30:07.986+02:00");
			CompruebaEstado(informacion_ajena_recibida, info_propia);

			//Coche.pila.addAlertaClimatica("3",Coche.pila,"AlertaClima1","1.0", "lluvia", "2009-05-19T11:10:12.585+02:00");
			CompruebaClima(informacion_ajena_recibida, info_propia);
			
			//Coche.pila.add("AlertaVehiculoEmergencias1","130.0","1.0","4","2.0","Emergencias2","2009-05-16T16:56:12.585+02:00");
			CompruebaVehiculoEmergencias(informacion_ajena_recibida,info_propia);
			
			
			//Coche.pila.add("AlertaAtasco1", "20","3.0" ,"5" ,"2.0" ,"Vehiculo5" , "2009-05-19T10:45:12.585+02:00");
			CompruebaFrenada(informacion_ajena_recibida, info_propia);


			//Coche.pila.addAlertaVeloMax("6", "AlertaVelMax1", "120","3.0","2009-05-19T11:08:45.296+02:00");
			CompruebaVelocidadMax(informacion_ajena_recibida, info_propia);

			
			Coche.pila.ordenaPila(Coche.pila);
			Coche.pila.muestraAlertas(Coche.pila);
		}

		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Método encargado de calcular el tiempo restante hasta que ocurra cierta situación (tiempo estimado hasta que un vehiculo que nos
	 * adelanta, tiempo estimado hasta encontrarnos con un vehículo parado,etc).
	 * @param mivel velocidad de nuestro vehiculo
	 * @param vel_veh velocidad del vehiculo al cual se quiere calcular el tiempo estimado
	 * @param midist_recorrida distancia recorrida por nuestro vehiculo
	 * @param dist_veh_a_recorrida distancia recorrida por el otro vehiculo
	 * @return String
	 */
	public static String calcula_tiempo_restante(String mivel,String vel_veh_a, String midist_recorrida,String dist_veh_a_recorrida, String mi_dir, String dir_ajena) 
	{
		final int LONGITUD_AUTOPISTA = 100;

		float f_mi_dist = Float.valueOf(midist_recorrida).floatValue();
		float f_dist_vehi = Float.valueOf(dist_veh_a_recorrida).floatValue();

		if (!mi_dir.equals(dir_ajena)) {
			// Ambos vehiculos siguen sentidos opuestos
			f_dist_vehi = LONGITUD_AUTOPISTA - f_dist_vehi;
		}

		// Diferencia de distancias en metros.
		float dif_distancias = (f_mi_dist - f_dist_vehi) * 1000;

		if (dif_distancias < 0)
			dif_distancias = dif_distancias * -1;
		// Lo hacemos positivo y lo pasamos a metros

		float f_mi_vel = Float.valueOf(mivel).floatValue();
		float f_vel_vehi = Float.valueOf(vel_veh_a).floatValue();
		float f_dif_velocidades = (f_mi_vel - f_vel_vehi) * (1000f / 3600f);
		if (f_dif_velocidades < 0)
			f_dif_velocidades *= -1;
		// Pasamos la velocidad a m/s multiplicando por 1000 / 3600

		float restante = (dif_distancias / f_dif_velocidades);
		String ETA_restante_str = Float.toString(restante);
		return ETA_restante_str;
	}
	
	/**
	 * Funcion auxiliar para concatenar un numero al nombre de la alerta en funcion del tamaño de la pila.
	 * Asi las alertas vendrian identificadas por "nombre del tipo de alerta + numero".
	 * @param nomAlerta nombre de la alerta a la cual concatenar el numero adecuado
	 * @param tam_pila tamaño de la pila en funcion del cual se asigna el numero adecuado.
	 * @see Comprobaciones#anadirAlerta(String, PilaEventos, String, String, String, String, String, String)
	 * @see Comprobaciones#anadirAlertaClimatica(String, PilaEventos, String, String, String, String)
	 * @see Comprobaciones#anadirAlertaVeloMax(String, PilaEventos, String, String, String)
	 * @see PilaEventos#add(String, Object, String, String, String, String, String)
	 * @see PilaEventos#addAlertaClimatica(String, PilaEventos, String, String, String, String)
	 * @see PilaEventos#addAlertaVeloMax(String, String, String, String)
	 * @return String
	 */
	public static String concatenaNombreAlerta(String nomAlerta, int tam_pila) 
	{
		int numAlerta = tam_pila + 1;
		String str_numAlerta = new String(Integer.toString(numAlerta));
		String nombreAlertaFinal = nomAlerta + str_numAlerta;
		return nombreAlertaFinal;
	}
	
	/**
	 * Metodo para comprobar si un vehiculo de emergencias se dispone a adelantar a nuestro vehiculo.
	 * Va a iterar sobre el Element info_propia para saber la informacion de nuestro vehiculo y luego sobre informacion_ajena_recibida para obtener la informacion ajena.
	 * Primero comprueba si se trata de un vehiculo de emergencias, luego comprueba que siga la misma direccion que nuestro vehiculo,
	 * despues comprueba si la distancia que separa a nuestro vehiculo de ese vehiculo de emergencias es la minima,
	 * es decir, si no existe otro vehiculo de emergencias que cumpla las condiciones anteriores y sea más cercano.
	 * En el caso de que haya dos vehiculos que estén igual de cerca, va a cojer el vehiculo cuya información sea más reciente en nuestro fichero XML.
	 * Posteriormente omprueba si el vehiculo de emergencias que se ha tomado, lleva una velocidad mayor que nuestro vehiculo, es decir,
	 * se dispone a adelantarnos.
	 * Finalmente, si ha habido algun vehiculo que ha cumplido las condiciones, se añade una alerta a la pila.
	 * @param informacion_ajena_recibida informacion que recibimos del fichero XML del vehiculo
	 * @param info_propia informacion propia almacenada en nuestro fichero XML.
	 * @see Comprobaciones#anadirAlerta(String,PilaEventos,String,String,String,String,String,String)
	 * @see PilaEventos#add(String, Object, String, String, String, String, String)
	 * @see PilaEventos#buscaYSustituyeAlerta(PilaEventos, ArrayList)
	 */
	
	public static void CompruebaVehiculoEmergencias(Element informacion_ajena_recibida, Element info_propia) 
	{

		String str_miclima = null;
		String str_tiempo_clima_a = null;
		String str_lugar_clima_a = null;
		Attribute tipo_vehiculo_a = null;
		String str_velocidad_vehiculo_a = null;
		String str_distancia_vehiculo_a = null;
		String str_mivel = null;
		String str_mitiempo = null;
		String str_midistancia = null;
		String str_midireccion = null;
		String str_direccion_veh_a = null;
		String tiempo = null;
		DateTime tiempo_vehiculo = null;
		String str_nombre_minimo = null;
		String str_distancia_minima = new String("999");
		String str_nombre_vehiculo = null;
		String str_velocidad_minimo = null;
		float f_mi_vel = 0;
		float f_vel_vehi = 0;
		// String str_tiempo_a = null;
		DateTime tiempo_mas_actual_dt = null;
		// String tiempo_mas_actual = new DateTime(1, 1, 1, 0, 0, 0, 0);
		String str_tiempo_mas_actual = null;
		String nombreAlerta = new String("AlertaVehiculoEmergencias");
		String prioridad = new String("4");

		Iterator i = info_propia.getChildren().iterator();

		// Itera sobre los atributos de la <informacion_propia>

		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			else if (e.getName().equals("tiempo")) {
				str_mitiempo = e.getValue();
			} else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
			else if (e.getName().equals("tiempo"))
				str_tiempo_clima_a = e.getValue();

		}

		Iterator it = informacion_ajena_recibida.getChildren().iterator();

		/*
		 * Itera sobre los vehiculos para encontrar uno de emergencias que se
		 * aproxime al nuestro. La idea es que encuentre uno, calcule la
		 * distancia, y si es menor de cierto valor, lanze el mensaje en
		 * pantalla. La distancia se va a calcular en función de la velocidad
		 * del vehiculo de emergencias.
		 */

		while (it.hasNext()) 
		{
			Element e = (Element) it.next();
			tipo_vehiculo_a = e.getAttribute("tipo");
			str_nombre_vehiculo = e.getAttributeValue("nombre");
			if ((tipo_vehiculo_a.getValue()).equals("Emergencias")) 
			{
				str_direccion_veh_a = e.getChildText("direccion");
				if (str_direccion_veh_a.equals(str_midireccion)) 
				{
					str_distancia_vehiculo_a = e.getChildText("distancia");
					str_velocidad_vehiculo_a = e.getChildText("velocidad");
					f_mi_vel = Float.valueOf(str_mivel).floatValue();
					f_vel_vehi = Float.valueOf(str_velocidad_vehiculo_a).floatValue();
					float flt_dist_a = Float.valueOf(str_distancia_vehiculo_a).floatValue();
					float mi_dist = Float.valueOf(str_midistancia).floatValue();
					float f_dif_velocidades = f_mi_vel - f_vel_vehi;
					if (f_dif_velocidades < 0)
					{
						/*
						 * Es una forma de filtrar para que no se añadan alertas si no hay
						 * vehiculo que nos adelante, puesto que si no hay vehiculo de
						 * emergencias, f_vel_vehi seria 0 y la diferencia seria > 0 y no
						 * entraria aqui. La velocidad del emergencias es mayor que la
						 * nuestra.
						 */
						if (flt_dist_a < mi_dist)
						{
							/*
							 * Si la distancia del vehiculo de emergencias es menor que la nuestra, 
							 * entonces continuamos comprobando
							 */
						
							float flt_distmin = Float.valueOf(str_distancia_minima).floatValue();							
							tiempo = e.getChildText("tiempo");

							if (flt_dist_a < flt_distmin) 
							{
								str_distancia_minima = str_distancia_vehiculo_a;
								str_nombre_minimo = str_nombre_vehiculo;
								str_velocidad_minimo = String.valueOf(f_vel_vehi);
								str_tiempo_mas_actual = tiempo;
								tiempo_mas_actual_dt = new DateTime(str_tiempo_mas_actual);
							}

							else if (flt_dist_a == flt_distmin)

							{
								/*
								 * En caso de tener la misma distancia, cogemos el
								 * vehiculo más actual
								 */

								tiempo_vehiculo = new DateTime(tiempo);
								tiempo_mas_actual_dt = new DateTime(str_tiempo_mas_actual);
								if (tiempo_vehiculo.isAfter(tiempo_mas_actual_dt)) 
								{
									/*
									 * El vehiculo actual es mas reciente que el que esta en
									 * mas_actual. Actualizamos la variable y el resto de
									 * datos a continuación.
									 */
									
									str_distancia_minima = str_distancia_vehiculo_a;
									tiempo_mas_actual_dt = tiempo_vehiculo;
									str_velocidad_minimo = String.valueOf(f_vel_vehi);
									str_nombre_minimo = str_nombre_vehiculo;
									str_tiempo_mas_actual = tiempo;
								}
							}
						}
					}
				}
			}
		}
		if (str_nombre_minimo!="")
		{
			anadirAlerta(Coche.pila,nombreAlerta,str_velocidad_minimo,str_distancia_minima,prioridad,str_midistancia,str_nombre_minimo,str_tiempo_mas_actual);
		}
	}

	/**
	 * Clase que va a comprobar si nuestro vehiculo se dispone a ser sobrepasado.
	 * <p>
	 * Primero comprueba si el vehiculo sigue la misma direccion que el nuestro y luego calcula la diferencia de velocidades, para pasar a comprobar
	 * si no se trata de un vehiculo de emergencias, para el cuál ya hay otro método (@see CompruebaVehiculoEmergencias(Element,Element)).
	 * Una vez comprobado esto, comprueba la diferencia de distancias entre el vehiculo y el nuestro, para ver si es el vehiculo que pretende adelantarnos más cercano a nosotros.
	 * En caso de haber dos vehiculos a la misma distancia que nosotros, se tomaria el que tiene su información más actual en nuestro fichero XML.
	 * Finalmente, si ha habido algun vehiculo que ha cumplido las condiciones, se añade una alerta a la pila.
	 * @param informacion_ajena_recibida informacion que recibimos del fichero XML del vehiculo
	 * @param info_propia informacion propia almacenada en nuestro fichero XML.
	 * @see Comprobaciones#anadirAlerta(String,PilaEventos,String,String,String,String,String,String)
	 * @see PilaEventos#add(String, Object, String, String, String, String, String)
	 * @see PilaEventos#buscaYSustituyeAlerta(PilaEventos, ArrayList)
	 */
	public static void CompruebaVehiculoAdelantando(Element informacion_ajena_recibida, Element info_propia) {
		final float DISTANCIA = 0.500f;
		/*
		 * Se establece la distancia a la que el dispositivo interpretará que el
		 * vehiculo está siendo adelantado en 100m.
		 */

		/*
		 * EVENTO DE ACTIVACION: El coche que va detrás nuestra, lleva una mayor
		 * velocidad que nosotros y se encuentra a una distancia menor de 100
		 * metros.
		 */

		String nombreAlerta = new String("AlertaAdelantando");
		String prioridad = new String("1");
		String str_miclima = null;
		String str_tiempo_clima_a = null;
		String str_lugar_clima_a = null;
		String str_mivel = null;
		String nombre_vehiculo_cercano = null;
		String str_mitiempo = null;
		String nombre_vehiculo = null;
		String tipo_vehiculo_a = null;
		String str_midistancia = null;
		String str_distancia_vehiculo_a = null;
		String str_velocidad_vehiculo_a = null;
		String str_midireccion = null;
		String str_direccion_vehiculo_a = null;
		String str_tiempo_vehiculo_a = null;
		DateTime dt_tiempo_mivehiculo = null;
		float dif_distancias_minima = 999.9f;
		String str_tiempo_a = null;
		String str_distancia_vehiculo_cercano = null;
		DateTime dt_tiempo_vehiculo_cercano = null;
		DateTime dt_tiempo_vehiculo_a;
		String str_tiempo_vehiculo_cercano = null;
		int dif_velocidades_maxima = 0;
		String str_velocidad_cercano = null;
		Iterator i = info_propia.getChildren().iterator();

		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
			else if (e.getName().equals("tiempo"))
				str_mitiempo = e.getValue();

		}

		Iterator it = informacion_ajena_recibida.getChildren().iterator();

		while (it.hasNext()) 
		{
			Element e = (Element) it.next();
			str_direccion_vehiculo_a = e.getChildText("direccion");
			if (str_direccion_vehiculo_a.equals(str_midireccion)) 
			{
				str_distancia_vehiculo_a = e.getChildText("distancia");
				float flt_dist_a = Float.valueOf(str_distancia_vehiculo_a).floatValue();
				float mi_dist = Float.valueOf(str_midistancia).floatValue();
				
				if (flt_dist_a < mi_dist)
				{
					str_velocidad_vehiculo_a = e.getChildText("velocidad");
					int vel_ajena = Integer.valueOf(str_velocidad_vehiculo_a).intValue();
					int mi_vel = Integer.valueOf(str_mivel).intValue();
					int dif_velocidades = vel_ajena - mi_vel;
					if (dif_velocidades > 0) 
					{
						tipo_vehiculo_a = e.getAttributeValue("tipo");
						if (!tipo_vehiculo_a.equals("Emergencias")) 
						{
							nombre_vehiculo = e.getAttributeValue("nombre");
							str_tiempo_vehiculo_a = e.getChildText("tiempo");
							str_velocidad_vehiculo_a = e.getChildText("velocidad");
							dt_tiempo_vehiculo_a = new DateTime(str_tiempo_vehiculo_a);

							//float midist = Float.valueOf(str_midistancia).floatValue();
							//float dis_aje = Float.valueOf(str_distancia_vehiculo_a).floatValue();
							float dif_distancias = mi_dist - flt_dist_a;
							if (dif_distancias > 0 && dif_distancias <= DISTANCIA) 
							{
								if (dif_distancias < dif_distancias_minima) 
								{
									/*
									 * Si el vehiculo detectado se encuentra más
									 * cerca del más cercano hasta ahora
									 */

									nombre_vehiculo_cercano = nombre_vehiculo;
									str_distancia_vehiculo_cercano = str_distancia_vehiculo_a;
									str_velocidad_cercano = str_velocidad_vehiculo_a;
									dif_distancias_minima = dif_distancias;
									str_tiempo_vehiculo_cercano = str_tiempo_vehiculo_a;
									dt_tiempo_vehiculo_cercano = new DateTime(str_tiempo_vehiculo_cercano);
								}

								else if (dif_distancias == dif_distancias_minima) 
								{
									/*
									 * Si da la casualidad que hay dos tomas de
									 * datos de un coche con la misma distancia,
									 * puede ser que el coche llegue a 1 km nuestra,
									 * y decelere, luego vuelva a 1 km. para
									 * adelantar. En ese caso, cocgemos la toma más
									 * actual.
									 */

									DateTime tiempo_vehiculo = new DateTime(str_tiempo_vehiculo_a);
									DateTime tiempo_mas_actual_dt = new DateTime(str_tiempo_vehiculo_cercano);
									if (tiempo_vehiculo.isAfter(tiempo_mas_actual_dt)) 
									{
										str_distancia_vehiculo_cercano = str_distancia_vehiculo_a;
										tiempo_mas_actual_dt = tiempo_vehiculo;
										str_velocidad_cercano = str_velocidad_vehiculo_a;
										nombre_vehiculo_cercano = nombre_vehiculo;
										str_tiempo_vehiculo_cercano = str_tiempo_vehiculo_a;
									}
								}
							}
						
							/*
							 * No vamos a calcular el tiempo restante para ser adelantados.
							 * Simplemente que muestre al usuario la alerta avsiandole de que va a
							 * ser adelantado.
							 */
							
						}
					}
				}
			}
		}
		if (nombre_vehiculo_cercano != null) 
		{
			//anadirAlerta(Coche.pila,nombreAlerta,str_velocidad_minimo,str_distancia_minima,prioridad,str_midistancia,str_nombre_minimo,str_tiempo_mas_actual);
			anadirAlerta(Coche.pila,nombreAlerta,str_velocidad_cercano,str_distancia_vehiculo_cercano,prioridad,str_midistancia,nombre_vehiculo_cercano,str_tiempo_vehiculo_cercano);
		}
	}
	/**
	 * Comprueba si hay algun vehiculo con velocidad 0 que siga nuestra misma direccion.
	 * En caso de haberlo, comprueba si es el más cercano, y si existen dos o más vehiculos a la misma distancia,
	 * se va a tomar el que tengo la información más actualizada en nuestro fichero XML, y a ese se le asigna como vehiculo más cercano por defecto.
	 * Finalmente, si ha habido algun vehiculo que ha cumplido las condiciones, se añade una alerta a la pila.
	 * @param informacion_ajena_recibida informacion que recibimos del fichero XML del vehiculo
	 * @param info_propia informacion propia almacenada en nuestro fichero XML.
	 * @see Comprobaciones#anadirAlerta(String,PilaEventos,String,String,String,String,String,String)
	 * @see PilaEventos#add(String, Object, String, String, String, String, String)
	 * @see PilaEventos#buscaYSustituyeAlerta(PilaEventos, ArrayList)
	 */
	public static void CompruebaEstado(Element informacion_ajena_recibida,Element info_propia) 
	{
		String prioridad = new String("2");
		String nombreAlerta = new String("AlertaEstado");
		String str_miclima = null;
		String str_tiempo_clima_a = null;
		String str_lugar_clima_a = null;
		String str_mivel = null;
		String str_mitiempo = null;
		String str_midistancia = null;
		String str_distancia_vehiculo_a = null;
		String str_nombre_vehiculo_a = null;
		String str_midireccion = null;
		String str_tiempo_vehiculo_a = null;
		String str_tiempo_mas_actual = null;
		float dif_distancias_minima = 999.9f;
		
		Iterator i = info_propia.getChildren().iterator();
		while (i.hasNext()) 
		{
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			else if (e.getName().equals("tiempo")) 
				str_mitiempo = e.getValue();
			else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
		}

		Iterator j = informacion_ajena_recibida.getChildren().iterator();
		// Itera sobre los vehiculos

		Attribute tipo_vehiculo_a = null;
		String velocidad_vehiculo_a = null;
		String direccion_vehiculo_a = null;
		String str_nombre_minimo = null;
		DateTime tiempo_vehiculo = null;
		String str_distancia_minima = null;
		DateTime mas_actual = new DateTime(1, 1, 1, 0, 0, 0, 0);
		String tiempo = null;
		while (j.hasNext()) 
		{
			Element e = (Element) j.next();
			velocidad_vehiculo_a = e.getChildText("velocidad");
			direccion_vehiculo_a = e.getChildText("direccion");
			if (velocidad_vehiculo_a.equals("0")) 
			{
				if (direccion_vehiculo_a.equals(str_midireccion)) 
				{
					str_distancia_vehiculo_a = e.getChildText("distancia");
					str_tiempo_vehiculo_a = e.getChildText("tiempo");
					str_nombre_vehiculo_a = e.getAttributeValue("nombre");
					float dist_aj = Float.valueOf(str_distancia_vehiculo_a).floatValue();
					float mi_dist = Float.valueOf(str_midistancia).floatValue();
					float dif_distancias = dist_aj - mi_dist;
					if (dif_distancias > 0)
					{
						/*
						 * Que el vehiculo esté delante, no detrás nuestra.
						 */

						if (dif_distancias < dif_distancias_minima) 
						{
							str_distancia_minima = str_distancia_vehiculo_a;
							str_nombre_minimo = str_nombre_vehiculo_a;
							dif_distancias_minima = dif_distancias;
							str_tiempo_mas_actual = str_tiempo_vehiculo_a;
							mas_actual = new DateTime(str_tiempo_vehiculo_a);
						}

						else if (dif_distancias == dif_distancias_minima)

						{
							tiempo_vehiculo = new DateTime(str_tiempo_vehiculo_a);
							if (tiempo_vehiculo.isAfter(mas_actual)) 
							{
								/*
								 * El vehiculo actual es mas reciente que el que
								 * esta en mas_actual. Actualizamos la variable
								 * y el resto de datos a continuación.
								 */

								mas_actual = tiempo_vehiculo;
								str_tiempo_mas_actual = str_tiempo_vehiculo_a;
								str_distancia_minima = str_distancia_vehiculo_a;
								str_nombre_minimo = str_nombre_vehiculo_a;
							}
						}
					}
				}
			}
		}

		if (str_nombre_minimo != null) 
		{
			String str_velocidad_vehiculo_minimo = new String("0");
			anadirAlerta(Coche.pila, nombreAlerta,str_velocidad_vehiculo_minimo,str_distancia_minima,prioridad,str_midistancia,str_nombre_vehiculo_a,str_tiempo_mas_actual);
		}
	}
	
	/**
	 * Este metodo va a comprobar si hay algun vehiculo delante nuestra y en nuestra misma dirección envuelto en una frenada brusca debido a un
	 * atasco repentino. 
	 * El algoritmo va a consistir en comprobar si la reduccion de velocidad es mayor de un cierto porcentaje (definido en una constante) de la velocidad del vehiculo desde
	 * el que se llama a éste método y qué esté dentro de un rango de distancia, en cuyo caso el vehiculo se va a añadir a un ArrayList.
	 * Si en ese array ya se encontraba el vehiculo, entonces se va a comprobar si existe en el fichero XML una información más actualizada de dicho vehiculo, en cuyo caso se sustituye la información del mismo que hay en el array.
	 * Cuando ya no haya más vehiculos con estas condiciones, se itera el arraylist comparando cada vehiculo con los que estan en el mismo arraylist y viendo si la distancia entre cada uno es menor de un valor (DISTANCIA_ATASCO). 
	 * Al final del algoritmo, se va a añadir a la pila como alerta aquel vehiculo que, estando dentro del array que forman los vehiculos atascados, se encuentre más proximo a nuestro vehiculo; y en caso
	 * de que haya varios igual de próximos, se tomará el que tenga la información más actualizada.
	 * @param informacion_ajena_recibida informacion que recibimos del fichero XML del vehiculo
	 * @param info_propia informacion propia almacenada en nuestro fichero XML.
	 * @see Comprobaciones#anadirAlerta(String,PilaEventos,String,String,String,String,String,String)
	 * @see PilaEventos#add(String, Object, String, String, String, String, String)
	 * @see PilaEventos#buscaYSustituyeAlerta(PilaEventos, ArrayList)
	 */
	public static void CompruebaFrenada(Element informacion_ajena_recibida,Element info_propia) 
	{
		/*
		 * Es la variable de tipo float que va a establecer la distancia entre vehiculos que va a considerarse como atasco.
		 * Es decir, si hay coches con una velocidad menor de la mitad de la de nuestro vehiculo que se encuentran separados
		 * entre ellos por menos de DISTANCIA_ATASCO, entonces se considera que dichos vehiculos forman parte de un atasco.
		 */
		final float DISTANCIA_MAXIMA_SEPARACION = 1.0f;
		final float DISTANCIA_ATASCO = 0.2f;
		final float PORCENTAJE_VELOCIDAD = 0.5f;
		String prioridad = new String("5");
		String nombreAlerta = new String("AlertaAtasco");
		String str_miclima = null;
		String str_tiempo_clima_a = null;
		String str_lugar_clima_a = null;
		String str_mivel = null;
		String str_mitiempo = null;
		DateTime dt_tiempo_mivehiculo = null;
		String str_midistancia = null;
		String str_midireccion = null;
		String velocidad_vehiculo_a = null;
		String distancia_vehiculo_a = null;
		String hora_proximo = null;
		String hora_vehiculo_atasco = null;
		String hora_vehiculo_a = null;
		String str_miaceleracion = null;
		String str_mivmax = null;

		// Recorriendo nuestros atributos

		Iterator i = info_propia.getChildren().iterator();

		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			else if (e.getName().equals("tiempo"))
				str_mitiempo = e.getValue();
			else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
			else if (e.getName().equals("aceleracion"))
				str_miaceleracion = e.getValue();
			else if (e.getName().equals("vmax"))
				str_mivmax = e.getValue();
		}

		// Recorriendo los atributos de los vehiculos recibidos

		ArrayList lista_vehiculos = new ArrayList();
		Iterator j = informacion_ajena_recibida.getChildren().iterator();

		// Itera sobre los vehiculos

		String direccion_vehiculo_a = null;
		String nombre_vehiculo_a = null;
		String direccion_vehiculo_nuestro = info_propia.getChildText("direccion");
		while (j.hasNext()) 
		{
			// Mientras haya vehiculos que leer de la <informacion_ajena>
			Element e = (Element) j.next();
			nombre_vehiculo_a = e.getAttribute("nombre").getValue();
			velocidad_vehiculo_a = e.getChildText("velocidad");
			direccion_vehiculo_a = e.getChildText("direccion");
			distancia_vehiculo_a = e.getChildText("distancia");
			hora_vehiculo_a = e.getChildText("tiempo");
				// Comprobamos que los vehiculos sigan nuestra misma direccion
				if (direccion_vehiculo_a.equals(direccion_vehiculo_nuestro)) 
				{
					float mi_distancia = Float.valueOf(str_midistancia).floatValue();
					float distancia_ajena = Float.valueOf(distancia_vehiculo_a).floatValue();
					// Comprobamos si la distancia es menor o igual a 1 km
					if (distancia_ajena - mi_distancia <= DISTANCIA_MAXIMA_SEPARACION) 
					{
						String distancia_vehiculo_atasco = distancia_vehiculo_a;
						String velocidad_vehiculo_atasco = velocidad_vehiculo_a;
						hora_vehiculo_atasco = hora_vehiculo_a;
						int vel_vehiculo_a = Integer.valueOf(velocidad_vehiculo_a).intValue();
						int mi_vel = Integer.valueOf(str_mivel).intValue();
						if (vel_vehiculo_a <= (mi_vel * PORCENTAJE_VELOCIDAD)) 
						{
							/*
							 * Se comprueba que la velocidad del vehiculo es la
							 * mitad de la nuestra actual, y añadimos su nombre
							 * y su distancia al arraylist.
							 */
							
							ListIterator it_comprueba_nombre = lista_vehiculos.listIterator();
							String nombre_comprueba = null;
							boolean existe = false;
							while (it_comprueba_nombre.hasNext() && !existe)
							{
								ArrayList comprueba = (ArrayList)it_comprueba_nombre.next();
								nombre_comprueba = (String)comprueba.get(0);
								if (nombre_comprueba.equals(nombre_vehiculo_a))
									existe = true;
							}
							if (!existe)
							{
								ArrayList vehiculo_atasco = new ArrayList();
								vehiculo_atasco.add(nombre_vehiculo_a);
								vehiculo_atasco.add(velocidad_vehiculo_atasco);
								vehiculo_atasco.add(distancia_vehiculo_atasco);
								vehiculo_atasco.add(hora_vehiculo_atasco);
								
								lista_vehiculos.add(vehiculo_atasco);
							}
							else
							{
								/*
								 * La lista de vehiculos ya contiene un vehiculo con ese nombre,
								 * con lo cual hemos de buscar si es el que tiene los datos más recientes.
								 * Si al que está en la lista es el más actual, se deja.
								 * Si no, se sustituye el que está en la lista por el nuevo.
								 */
								ListIterator it_vehiculo = lista_vehiculos.listIterator();
								boolean enc = false;
								String nombre_vehiculo_enlista = null;
								String tiempo_vehiculo_enlista = null;
								ArrayList v = new ArrayList();
								while (it_vehiculo.hasNext() && !enc)
								{
									v = (ArrayList)it_vehiculo.next();
									nombre_vehiculo_enlista = (String)v.get(0);
									tiempo_vehiculo_enlista = (String)v.get(3);
									if (nombre_vehiculo_enlista.equals(nombre_vehiculo_a))
										enc = true;
								}
								
								DateTime dt_tiempo_vehiculo_atasco = new DateTime(tiempo_vehiculo_enlista);
								DateTime dt_tiempo_vehiculo_nuevo = new DateTime(hora_vehiculo_atasco);
								if (dt_tiempo_vehiculo_nuevo.isAfter(dt_tiempo_vehiculo_atasco))
								{
									/*
									 * El vehiculo iterador tiene información más actual que el que ya hay en la lista,
									 * asi que vamos a actualizar su información.
									 */
									ListIterator it_actualizar = lista_vehiculos.listIterator();
									boolean enc1 = false;
									while (it_actualizar.hasNext() && !enc1)
									{
										ArrayList a = (ArrayList)it_actualizar.next();
										String nombre_vehi = (String)a.get(0);
										if (nombre_vehi.equals(nombre_vehiculo_a))
										{
											a.set(1,velocidad_vehiculo_atasco);
											a.set(2,distancia_vehiculo_atasco);
											a.set(3,hora_vehiculo_atasco);
											it_actualizar.set(a);
										}
									}
								}
							}
						}
					}
				}
			}

		/*
		 * Ahora iteramos por el ArrayList para comprobar las distancias de y a
		 * cada vehiculo respectivamente. Creamos dos iteradores y vamos
		 * iterando el ArrayList, comprobando que el elemento al que apunta el
		 * iterador no se el mismo que el elemento al que apunta el otro
		 * iterador.
		 */

		Iterator it_array1 = lista_vehiculos.iterator();
		Iterator it_array2 = lista_vehiculos.iterator();
		/*
		 * Estos iteradores iteran en un array que contiene a su vez, ArrayLists
		 */
		String distancia1 = null;
		String distancia2 = null;
		String nombre_vehiculo1 = null;
		String nombre_vehiculo2 = null;
		String tiempo_proximo = null;
		String tiempo1 = null;
		String tiempo2 = null;
		int num_coches_atasco = 0;
		String vel_proximo = null;
		String dist_proximo = new String("999");
		String nombre_proximo = null;

		while (it_array1.hasNext()) 
		{
			ArrayList a1 = (ArrayList) it_array1.next();
			nombre_vehiculo1 = (String) a1.get(0);
			distancia1 = (String) a1.get(2);
			tiempo1 = (String) a1.get(3);

			// Comprobamos si la distancia de este vehiculo es menor que la del
			// vehiculo más proximo hasta ahora.
			if (distancia1.compareTo(dist_proximo) < 0) 
			{
				vel_proximo = (String) a1.get(1);
				dist_proximo = distancia1;
				nombre_proximo = nombre_vehiculo1;
				tiempo_proximo = tiempo1;
			}
			else if (distancia1.compareTo(dist_proximo)==0)
			{
				DateTime tiempo1_dt = new DateTime (tiempo1);
				DateTime tiempo_proximo_dt = new DateTime (tiempo_proximo);
				if (tiempo1_dt.isAfter(tiempo_proximo_dt))
				{
					/*
					 * El tiempo1 es más actual, a igualdad de distancias.
					 */
					vel_proximo = (String) a1.get(1);
					dist_proximo = distancia1;
					nombre_proximo = nombre_vehiculo1;
					tiempo_proximo = tiempo1;
				}
			}
			while (it_array2.hasNext()) 
			{
				ArrayList a2 = (ArrayList) it_array2.next();
				nombre_vehiculo2 = (String) a2.get(0);
				distancia2 = (String) a2.get(2);
				tiempo2 = (String) a2.get(3);
				if (distancia2.compareTo(dist_proximo) < 0) 
				{
					vel_proximo = (String) a2.get(1);
					dist_proximo = distancia2;
					nombre_proximo = nombre_vehiculo2;
					tiempo_proximo = tiempo2;
				}
				else if (distancia2.compareTo(dist_proximo)==0)
				{
					DateTime tiempo2_dt = new DateTime (tiempo2);
					DateTime tiempo_proximo_dt = new DateTime (tiempo_proximo);
					if (tiempo2_dt.isAfter(tiempo_proximo_dt))
					{
						/*
						 * El tiempo1 es más actual, a igualdad de distancias.
						 */
						vel_proximo = (String)a2.get(1);
						dist_proximo = distancia2;
						nombre_proximo = nombre_vehiculo2;
						tiempo_proximo = tiempo2;
					}
				}
				
				/*
				 * Comprobamos si ambos iteradores apuntan al mismo elemento, en
				 * cuyo caso pasamos al siguiente elemento.
				 */

				if (!nombre_vehiculo1.equals(nombre_vehiculo2)) 
				{
					float dist1 = Float.valueOf(distancia1).floatValue();
					float dist2 = Float.valueOf(distancia2).floatValue();
					float dif_distancias = new Float(dist1 - dist2);
					/*
					 * En el caso de que sea negativa la diferencia, la hacemos positiva.
					 */
					if (dif_distancias<0)
						dif_distancias = dif_distancias* -1;
					
					
					/*
					 * Si la distancia entre los vehiculos es < DISTANCIA_ATASCO,
					 * entonces decimos que hay un coche más en el atasco.
					 */

					if (dif_distancias <= DISTANCIA_ATASCO)
						if (num_coches_atasco == 0)
							num_coches_atasco += 2;
						else
							num_coches_atasco++;
					/*
					 * Si la distancia entre los vehiculos del array es menor de DISTANCIA_ATASCO,
					 * se incrementa la variable num_coches_atasco
					 */
				}
			}
		}
		if (nombre_proximo!= "")
			anadirAlerta(Coche.pila,nombreAlerta,vel_proximo,dist_proximo,prioridad,str_midistancia,nombre_proximo,tiempo_proximo);
		//}
		}
	/**
	 * Metodo que comprueba si hay un cambio de clima en el camino que le queda por recorrer a nuestro vehiculo.
	 * Comprueba primero que el vehiculo siga nuestra misma direccion, para a continuación comprobar si tiene un clima distinto al nuestro.
	 * En ese caso, comprueba la distancia entre dicho vehiculo y el nuestro para ver si es el más cercano.
	 * Si existen dos o más vehiculos con la misma distancia, se toma el que tiene la información más actual en nuestro fichero XML y se le asigna como el vehiculo más cercano.
	 * Finalmente, si ha habido algun vehiculo que ha cumplido las condiciones, se añade una alerta a la pila.
	 * @param informacion_ajena_recibida
	 * @param info_propia
	 * @see Comprobaciones#anadirAlertaClimatica(String, PilaEventos, String, String, String, String)
	 * @see PilaEventos#addAlertaClimatica(String, PilaEventos, String, String, String, String)
	 */
	public static void CompruebaClima(Element informacion_ajena_recibida,Element info_propia) 
	{
		String str_distancia_minima_vehiculo = null;
		String prioridad = new String("3");
		String nomAlerta = new String("AlertaClima");
		final int LONGITUD_AUTOPISTA = 100;
		boolean add_alerta = false;
		String str_distancia_hasta_clima = null;
		String str_mivel = null;
		String str_vmax = null;
		String str_mitiempo = null;
		Interval intervalo_minimo = null;
		String str_midistancia = null;
		String str_midireccion = null;
		float flt_dist_coche_a;
		String str_miclima = null;
		String str_clima_a = null;
		String tiempo_actual = null;
		float flt_LONGITUD_AUTOPISTA;
		String distancia_coche_a = null;
		float flt_dist_hasta_clima = 0;
		String clima_de_menor_distancia = null;
		float flt_menor_distancia_hasta_clima = 999.9f;
		float flt_mi_distancia;
		DateTime dt_clima_mas_actual = new DateTime(1, 1, 1, 0, 0, 0, 0);
		Iterator i = info_propia.getChildren().iterator();

		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			if (e.getName().equals("vmax"))
				str_vmax = e.getValue();
			else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
			else if (e.getName().equals("tiempo"))
				str_mitiempo = e.getValue();
		}

		Iterator j = informacion_ajena_recibida.getChildren().iterator();
		// Itera sobre los vehiculos

		String direccion_vehiculo_a = null;
		while (j.hasNext()) {
			Element e = (Element) j.next();
			direccion_vehiculo_a = e.getChildText("direccion");

			if (direccion_vehiculo_a.equals(str_midireccion)) 
			{
				/*
				 * Comprobamos primero que el vehiculo venga en la misma
				 * direccion que nosotros, porque queremos su clima, aunque nos
				 * lo de uno que viene en sentido contrario
				 */

				str_clima_a = e.getChildText("clima");

				/*
				 * Ahora comprobamos si hay cambio de clima.
				 */

				if (!str_miclima.equals(str_clima_a)) 
				{
					/*
					 * Hemos recibido informacion de un vehiculo con un clima
					 * diferente. Ahora hemos de averiguar el lugar aproximado y
					 * el momento, segun los datos recibidos del vehiculo.
					 */

					distancia_coche_a = e.getChildText("distancia");
					String tiempo_coche_a = e.getChildText("tiempo");
					float flt_midistancia = Float.valueOf(str_midistancia).floatValue();
					flt_dist_coche_a = Float.valueOf(distancia_coche_a).floatValue();
					flt_dist_hasta_clima = flt_dist_coche_a - flt_midistancia;
					if (flt_dist_hasta_clima > 0) 
					{
						if (flt_dist_hasta_clima < flt_menor_distancia_hasta_clima) 
						{
							flt_menor_distancia_hasta_clima = flt_dist_hasta_clima;
							clima_de_menor_distancia = str_clima_a;
							dt_clima_mas_actual = new DateTime(tiempo_coche_a);
							tiempo_actual = tiempo_coche_a;

							/*
							 * Si se cumple que el coche viaja en nuestro
							 * sentido y que tiene la menor distancia de los
							 * anteriores, entonces se actualizan las variables.
							 */
						}

						else if (flt_dist_hasta_clima == flt_menor_distancia_hasta_clima) 
						{
							/*
							 * En caso de igualdad de distancias, se mira el
							 * vehiculo con la infor mas actual
							 */
							DateTime dt_tiempo_a = new DateTime(tiempo_coche_a);
							if (dt_tiempo_a.isAfter(dt_clima_mas_actual))
							{
								dt_clima_mas_actual = dt_tiempo_a;
								tiempo_actual = tiempo_coche_a;
								clima_de_menor_distancia = str_clima_a;
								flt_dist_coche_a = Float.valueOf(distancia_coche_a).floatValue();
								flt_mi_distancia = Float.valueOf(str_midistancia).floatValue();
								flt_LONGITUD_AUTOPISTA = Float.valueOf(LONGITUD_AUTOPISTA).intValue();
								flt_dist_hasta_clima = flt_dist_coche_a-flt_mi_distancia;
							}
						}
						str_distancia_hasta_clima = String.valueOf(flt_menor_distancia_hasta_clima);
					}
				}
			}
		}
		if (clima_de_menor_distancia != null)
			anadirAlertaClimatica(prioridad, Coche.pila, nomAlerta,str_distancia_hasta_clima, clima_de_menor_distancia,tiempo_actual);
	}

	/**
	 * Metodo que comprueba si la velocidad actual de nuestro vehiculo es mayor que la permitida actualmente, y además, comprueba si delante en la carretera
	 * hay algun vehiculo que tenga una velocidad maxima con una limitacion distinta a la nuestra.
	 * La forma de lectura de la vmax es una forma de medicion pasiva: simulacion de la lectura de una señal por un dispositivo en el vehiculo. 
	 * El objetivo es que los coches que vengan en sentido contrario nos avisen con antelación si han recibido informacion de un vehiculo que viene en nuestro sentido con una limitacion de velocidad, 
	 * de tal manera que tengamos conocimiento de esto con antelación.
	 * <p>
	 * 
	 * Primero, comprueba nuestra velocidad máxima, segun la lectura del dispositivo del vehiculo, y si esta superandose,
	 * se añade una alerta a la pila.
	 * Despues se comprueba en el fichero XML de nuestro vehiculo, la información del resto de vehiculo para ver si alguno
	 * que siga nuestra misma direccion tiene una limitacion de velocidad distinta a la nuestra actual.
	 * En ese caso, se comprueba la distancia de éste vehiculo hasta el nuestro, y si es el más cercano, se añade una alerta.
	 * En caso de que haya varios vehiculos a la misma distancia, se toma el que tenga la información en nuestro XML más actualizada.
	 * @param informacion_ajena_recibida
	 * @param info_propia
	 * @see Comprobaciones#anadirAlertaVeloMax(String, PilaEventos, String, String, String)
	 * @see PilaEventos#addAlertaVeloMax(String, String, String, String)
	 */
	public static void CompruebaVelocidadMax(Element informacion_ajena_recibida, Element info_propia)
	{
		float flt_dif_distancias = 999.9f;
		String str_mivel = null;
		String str_vmax = null;
		String str_midistancia = null;
		String str_midireccion = null;
		String str_miclima = null;
		String str_clima_a = null;
		String str_vmax_a = null;
		String hora = null;
		String hora_minima = null;
		String str_dist_vehiculo_a = null;
		String nomAlerta = null;
		String str_menor_distancia = null;
		String str_mitiempo = null;
		float flt_menor_distancia = 999.9f;
		String vmax_adecuada = null;
		boolean anadirAlerta = false;
		Interval minimo_intervalo = null;
		Iterator i = info_propia.getChildren().iterator();

		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (e.getName().equals("velocidad"))
				str_mivel = e.getValue();
			if (e.getName().equals("vmax"))
				str_vmax = e.getValue();
			else if (e.getName().equals("distancia"))
				str_midistancia = e.getValue();
			else if (e.getName().equals("direccion"))
				str_midireccion = e.getValue();
			else if (e.getName().equals("clima"))
				str_miclima = e.getValue();
			else if (e.getName().equals("tiempo"))
				str_mitiempo = e.getValue();
		}

		String prioridad = new String("6");
		nomAlerta = new String("AlertaVelMax");

		/*
		 * Primero comprobamos si nuestra velocidad actual es mayor que la
		 * permitida, en cuyo caso añadimos una nueva alerta o modificamos una
		 * existente.
		 */

		if (str_mivel.compareTo(str_vmax) > 0)
			anadirAlertaVeloMax(prioridad, Coche.pila, str_vmax, nomAlerta, "",str_mitiempo);
		else 
		{

		}
		Iterator j = informacion_ajena_recibida.getChildren().iterator();
		// Itera sobre los vehiculos

		String direccion_vehiculo_a = null;
		while (j.hasNext()) 
		{
			Element e = (Element) j.next();
			direccion_vehiculo_a = e.getChildText("direccion");
			if (str_midireccion.equals(direccion_vehiculo_a)) 
			{
				/*
				 * Comprobamos que la informacion es de un vehiculo que esta
				 * siguiendo nuestro sentido.
				 */

				str_vmax_a = e.getChildText("vmax");
				if (!str_vmax.equals(str_vmax_a))
				{
					str_dist_vehiculo_a = e.getChildText("distancia");
					hora = e.getChildText("tiempo");
					float distancia_ajena = Float.valueOf(str_dist_vehiculo_a).floatValue();
					float mi_dist = Float.valueOf(str_midistancia).floatValue();
					String dif_distancias = String.valueOf(distancia_ajena - mi_dist);
					if (str_dist_vehiculo_a.compareTo(str_midistancia) > 0) 
					{
						/*
						 * Si estamos por delante del vehiculo. En este caso, si
						 * la distancia entre ese vehiculo y el nuestro es la
						 * menor (el vehiculo más cercano a nosotros)....
						 */

						flt_dif_distancias = Float.valueOf(dif_distancias).floatValue();
						if (flt_dif_distancias < flt_menor_distancia) 
						{
							anadirAlerta = true;
							flt_menor_distancia = flt_dif_distancias;
							hora_minima = hora;
							str_menor_distancia = String.valueOf(flt_menor_distancia);
							vmax_adecuada = str_vmax_a;
						}
					}

					else if (str_dist_vehiculo_a.compareTo(str_menor_distancia) == 0) 
					{
						/*
						 * En el caso de que haya otro vehiculo con la misma
						 * distancia (normalmente el mismo vehiculo) cogemos la
						 * toma de datos más actual.
						 */

						DateTime tiempo_a = new DateTime(hora);
						DateTime tiempo_mas_actual = new DateTime(hora_minima);
						if (tiempo_mas_actual.isBefore(tiempo_a)) 
						{

							/*
							 * Si el intervalo entre nosotros y el otro vehiculo
							 * es menor que el minimo
							 */

							anadirAlerta = true;
							flt_dif_distancias = Float.valueOf(dif_distancias).floatValue();
							flt_menor_distancia = flt_dif_distancias;
							hora_minima = hora;
							str_menor_distancia = String.valueOf(flt_menor_distancia);
							vmax_adecuada = str_vmax_a;
						}
					}
				}
			}
		}

		if (anadirAlerta)
			anadirAlertaVeloMax(prioridad, Coche.pila, vmax_adecuada,nomAlerta, str_menor_distancia,hora_minima);
	}
	
	/**
	 * Metodo generico para añadir alertas. El numero de la alerta se calcula en función de las alertas que tenga la cola y se le concatena al nombre de
	 * alerta pasado por parametros a éste método. 
	 * Posteriormente se crea un array que contendrá dos elementos: el nombre de la alerta y el tiempo restante para que finalice la alerta. Finalmente, se añade a la cola.
	 *
	 * @param prioridad prioridad de la alerta. Cada tipo de alerta tiene su prioridad en funcion del tamaño de la pila.
	 * @param pila pila donde se añadirán las alertas.
	 * @param nomAlerta nombre de la alerta añadir. En los metodos de comprobacion de cada tipo de alerta, se asignará el valor adecuado a este parámetro en función del tipo de alerta.
	 * @param velocidad_vehiculo_minimo velocidad del vehiculo más cercano a nosotros (el que genera la alerta).
	 * @param mi_distancia distancia recorrida por nuestro vehiculo 
	 * @param distancia_minima distancia recorrida por el vehiculo más cercano a nosotros (el que genera la alerta).
	 * @param nombre_vehiculo nombre del vehiculo más cercano a nosotros (el que genera la alerta).
	 * @param tiempo marca de tiempo para comprobar la antigüedad de la información.
	 * @see Comprobaciones#Comprobaciones(String)
	 * @see Comprobaciones#CompruebaClima(Element, Element)
	 * @see Comprobaciones#CompruebaEstado(Element, Element)
	 * @see Comprobaciones#CompruebaFrenada(Element, Element)
	 * @see Comprobaciones#CompruebaVehiculoAdelantando(Element, Element)
	 * @see Comprobaciones#CompruebaVehiculoEmergencias(Element, Element)
	 * @see PilaEventos#buscaYSustituyeAlerta(PilaEventos, ArrayList)
	 */
																			
	public static boolean anadirAlerta(PilaEventos pila,String nomAlerta,String velocidad_vehiculo_minimo,String distancia_minima,String prioridad,String mi_distancia,String nombre_vehiculo,String tiempo)
	{
		/*
		 * Se añade la alerta. 
		 * La clase PilaEventos se encargará de no duplicar alertas.
		 */
		boolean added = false;
		String nombreAlertaFinal = concatenaNombreAlerta(nomAlerta, Coche.pila.size());
		added = pila.add(nombreAlertaFinal,velocidad_vehiculo_minimo,distancia_minima,prioridad,mi_distancia,nombre_vehiculo, tiempo);
		return added;
	}
	
	/**
	 * Metodo para añadir una velocidad maxima a la pila de alertas del vehiculo.
	 * Es llamado desde {@link Comprobaciones#CompruebaVelocidadMax(Element,Element)} y va a llamar al método {@link Comprobaciones#compruebaDuplicacionAlertaVeloMax(PilaEventos,String)} para comprobar si la alerta que queremos añadir ya está en la pila.
	 * En caso de que no esté, se va a añadir la nueva alerta, concatenandole al nombre de la misma un numero segun el tamaño actual de la pila y finalmente se llamará al método de la clase PilaEventos {@link PilaEventos#addAlertaVeloMax(String,String,String,String)}.
	 * @param prioridad prioridad de la alerta
	 * @param pila pila donde se añadirá la alerta
	 * @param str_vmax velocidad maxima de la nueva alerta
	 * @param nomAlerta nombre de la alerta
	 * @param distancia_aprox distancia aproximada hasta el punto donde está la nueva limitacion de la velocidad.
	 * @see Comprobaciones#CompruebaVelocidadMax(Element,Element)
	 * @see Comprobaciones#compruebaDuplicacionAlertaVeloMax(PilaEventos,String)
	 * @see PilaEventos#addAlertaVeloMax(String,String,String,String)
	 */
	public static boolean anadirAlertaVeloMax(String prioridad, PilaEventos pila,String str_vmax, String nomAlerta, String distancia_aprox,String hora) 
	{
		boolean added = false;
		/*
		 * Solo va a haber una alerta por velocidad maxima propia, que es cuando
		 * sepamos que EN ESE MOMENTO estamos sobrepasando el limite de
		 * velocidad.
		 */

		boolean AlertaVelocidadDuplicada = compruebaDuplicacionAlertaVeloMax(pila, str_vmax, distancia_aprox, hora);

		/*
		 * La duplicacion se tiene en cuenta en base a si hay alguna alerta de
		 * velocidad maxima con la misma vmax
		 */

		if (!AlertaVelocidadDuplicada) 
		{
			String nombreAlertaFinal = concatenaNombreAlerta(nomAlerta,Coche.pila.size());
			added = pila.addAlertaVeloMax(prioridad, nombreAlertaFinal, str_vmax, distancia_aprox, hora);
		}
		return added;
		/*
		 * Se comprueba si existe una alerta ya en la pila con una velocidad, en
		 * cuyo caso se cambia.
		 */
	}

	/**
	 * Metodo para añadir una alerta climatica a la pila.
	 * Lo primero que hace es comprobar si ya existe en la pila una alerta con los datos de la alerta que se quiere añadir como nueva.
	 * Si no existe, entonces se añade a la pila como nueva alerta.
	 * @param prioridad prioridad que se le dará a la alerta. Cada tipo de alerta tiene su prioridad predeterminada.
	 * @param pila pila a la que añadir la alerta
	 * @param nomAlerta nombre de la alerta
	 * @param str_distancia_hasta_clima distancia aproximada hasta donde ha ocurrido el cambio de clima
	 * @param str_clima_a clima nuevo del cual se genera la alerta
	 * @param tiempo marca de tiempo
	 * @see Comprobaciones#compruebaDuplicacionAlertaClimatica(String, String, String, String)
	 * @see Comprobaciones#CompruebaClima(Element, Element)
	 * @see PilaEventos#addAlertaClimatica(String, PilaEventos, String, String, String, String)
	 */
	public static boolean anadirAlertaClimatica(String prioridad,PilaEventos pila, String nomAlerta,String str_distancia_hasta_clima, String str_clima_a, String tiempo) {
		boolean added = false;
		boolean AlertaClimaticaDuplicada = compruebaDuplicacionAlertaClimatica(nomAlerta, str_distancia_hasta_clima, str_clima_a, tiempo);

		/*
		 * Con esta funcion vamos a comprobar que no existe ya una alerta
		 * climatica con la misma distancia de separacion con nuestro coche y el
		 * mismo clima.
		 */

		if (!AlertaClimaticaDuplicada) {
			String nombreAlertaFinal = concatenaNombreAlerta(nomAlerta,Coche.pila.size());
			added = pila.addAlertaClimatica(prioridad, pila, nombreAlertaFinal,str_distancia_hasta_clima, str_clima_a, tiempo);
		}
		return added;
	}

	/**
	 * Metodo para la comprobacion de la duplicacion de una alerta de velocidad maxima.
	 * Recorre la pila buscando si existe ya una alerta con la velocidad maxima pasada por parametro
	 * @param pila pila que se recorre para buscar la alerta duplicada.
	 * @param str_vmax velocidad maxima que buscamos en la alerta para comprobar si ya está duplicada.
	 * @return boolean
	 * @see Comprobaciones#CompruebaVelocidadMax(Element, Element)
	 * @see PilaEventos#addAlertaVeloMax(String, String, String, String)
	 */
	public static boolean compruebaDuplicacionAlertaVeloMax(PilaEventos pila,String str_vmax,String distancia,String hora) 
	{
		boolean duplicada = false;
		ListIterator it = pila.listIterator();
		while (it.hasNext()) 
		{
			ArrayList alerta = (ArrayList) it.next();
			String velocidad_max = (String) alerta.get(2);
			String hora_pila = (String) alerta.get(4);
			String dist_pila = (String) alerta.get(3);
			if (dist_pila.equals(distancia))
			{
				/*
				 * Distancia igual. Hay que comprobar si tienen la misma velocidad y hora;
				 * Si tienen la misma velocidad, se actualiza solo la hora;
				 * Si tiene distinta velocidad y hora, se actualizan las dos
				 */
				if (velocidad_max.equals(str_vmax))
				{
					if (!hora_pila.equals(hora))
					{
						/*
						 * Actualizamos solo la hora
						 */
						alerta.set(4,hora);
						it.set(alerta);
						duplicada = true;
					}
					/*
					 * Como tienen la misma distancia y velocidad y hora, no se hace nada.
					 */
				}
				else
				{
					/*
					 * Velocidad y hora son distintas; actualizar ambas.
					 */
					alerta.set(2,str_vmax);
					alerta.set(4,hora);
					it.set(alerta);
					duplicada = true;
				}
			}
				/*
				 * Ambas distancia son DISTINTAS;
				 * esta variable va a hacer que se cree una nueva alerta
				 */
			}
		return duplicada;
	}

	/**
	 * Metodo que comprueba si existe una alerta meteorológica duplicada.
	 * Recorre la pila de alertas en busca de alertas meterológicas, y comprueba el punto kilométrico aproximado donde ocurrió esa alerta.
	 * Si el punto está fuera de un intervalo de error que puede ocurrir, entonces se considera una nueva alerta y se llama al método correspondiente para añadir una nueva alerta a la pila.
	 * En caso de que la alerta esté dentro de ese intervalo, se comprueba si la marca de tiempo de la alerta en pila es más actual o menos que la nueva alerta, y si la nueva alerta es más actual, se actualiza la información de la alerta en pila, intercambiandola
	 * por la de la nueva alerta que se quiere añadir.
	 * @param nomAlerta nombre de la alerta. Va a ser "AlertaClima" puesto que en la pila se va a buscar en las alertas que contengan dicho nombre.
	 * @param str_distancia_hasta_clima distancia aproximada hasta el punto kilométrico donde puede haber ocurrido el cambio meteorológico. Hay un cierto márgen de error de aproximadamente 1 km.
	 * @param str_clima_a clima nuevo
	 * @param tiempo marca de tiempo
	 * @see Comprobaciones#CompruebaClima(Element, Element)
	 * @see PilaEventos#addAlertaClimatica(String, PilaEventos, String, String, String, String)
	 * @return boolean
	 */
	public static boolean compruebaDuplicacionAlertaClimatica(String nomAlerta,String str_distancia_hasta_clima, String str_clima_a, String tiempo) {
		boolean duplicada = false;
		ListIterator it = Coche.pila.listIterator();
		while (it.hasNext()) 
		{
			ArrayList alerta = (ArrayList) it.next();
			String nombre_alerta = (String) alerta.get(1);
			if (nombre_alerta.contains("AlertaClima")) 
			{
				String distancia_en_alerta = (String) alerta.get(2);
				String clima = (String) alerta.get(3);
				String tiempo_alerta = (String) alerta.get(4);

				float distancia_alerta = Float.valueOf(distancia_en_alerta).floatValue();
				//Distancia desde nuestro vehiculo a la alerta que hay en pila.
				float distancia_ta_clima = Float.valueOf(str_distancia_hasta_clima).floatValue();
				//Distancia desde nuestro vehiculo a la alerta que se ha detectado nueva.
				float diferencia = distancia_alerta - distancia_ta_clima;
				if (diferencia> 1.0 || diferencia <-1.0)
					
				/*
				 * Esta diferencia se refiere a la diferencia de distancias entre la alerta que hay en la pila 
				 * y la nueva alerta que se ha detectado.
				 * Esta sentencia condicional es para comprobar si existe una diferencia de aprox. 1 km entre ambas alertas,
				 * en cuyo caso se crearia una alerta nueva; si la diferencia entre ellas es menor, se sustituiría la antigua
				 * alerta por la nueva SIEMPRE Y CUANDO la marca temporal de la nueva fuese más actual.
				 */
				{

				/*
				* Si el clima que queremos añadir, está o 1 km más adelante
				* del que hay ahora o 1 km más atrás, entonces creamos
				* nueva alerta, DEBERIA DEVOLVER FALSE ESTA FUNCION PARA QUE SE CREASE UNA NUEVA.
				*/
				} 
				else 
				{
					/*
					 * Susituimos la alerta
					 */

					DateTime tiempo_alerta_dt = new DateTime(tiempo_alerta);
					DateTime tiempo_pila_dt = new DateTime(tiempo);
					if (tiempo_alerta_dt.isBefore(tiempo_pila_dt)) 
					{
						alerta.set(1, nombre_alerta);
						alerta.set(2, str_distancia_hasta_clima);
						alerta.set(3, str_clima_a);
						alerta.set(4, tiempo);
						it.set(alerta);
						duplicada = true;
					}
				}
			}
		}
		return duplicada;
	}
}