package xml;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.joda.time.DateTime;

/**
 * Esta clase será la encargada de gestionar todo lo relacionado con la manipulacion, lectura, modificacion y escritura de los ficheros XML.
 * 
 * @see manejaXML#concatenaXML(String, String)
 * @see manejaXML#leerXML(String)
 * @author Manuel Lorenzo
 */

public class manejaXML 
{
	static Document docRecibido;
	static Document docFinalMio;
	static final String NOM_VEHICULO = "Vehiculo1";

	/*
	 * Esta cadena simboliza el nombre de NUESTRO VEHICULO, para que pueda ser
	 * facilmente cambiado en cada caso. Su misión es para ver si nuestro
	 * vehiculo se encuentra ya en el elemento <informacion_ajena> del XML que
	 * recibimos.
	 */
	
	/**
	 * Este metodo va a leer el documento XML de nuestro vehiculo, mostrandolo por pantalla en la consola.
	 * @param filename nombre del fichero que se va a leer (documento XML)
	 * @return Document fichero XML
	 */
	public static Document leerXML(String filename) 
	{
		try 
		{
			SAXBuilder builder = new SAXBuilder(false);
			docRecibido = builder.build(filename);
			Element raiz = docRecibido.getRootElement();
			System.out.println("Vehiculo tipo: "+ raiz.getAttributeValue("tipo"));
			System.out.println("Nombre del vehiculo: "+ raiz.getAttributeValue("nombre"));
			List info_propia = raiz.getChildren("informacion_propia");
			System.out.println("Informacion propia");
			Iterator i = info_propia.iterator();
			while (i.hasNext()) 
			{
				Element e = (Element) i.next();
				Element infor_vel = e.getChild("velocidad");
				Element infor_acel = e.getChild("aceleracion");
				Element infor_direc = e.getChild("direccion");

			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return docRecibido;
	}
	
	/**
	 * Este método será el que, recibiendo dos ficheros XML, concatenará la informacion del recibido en el propio del vehiculo.
	 * Para poder gestionar más facilmente las concatenaciones de la informacion en los ficheros XML, se contemplan dos casos:
	 * Caso 1): Nuestro vehiculo tiene la <informacion_ajena> vacia (aun no ha contactado con nadie) y recibe un XML. Posibles casos:
	 * 
	 * 1) XML recibido con <informacion_ajena> vacia (ambos ficheros iguales: solo <info_propia>):
	 * 
	 * Añadir la <informacion_propia> del XML recibido a la <informacion_ajena> de nuestro vehiculo.
	 * 
	 * 2) XML recibido con<informacion_ajena> llena de otros vehiculos:
	 * 
	 * Añadir la <informacion_ajena> del XML recibido. Habria que ver si aparece nuestro vehiculo y filtrarlo.
	 * 
	 * @param fileNameMiVehiculo nombre del fichero XML que contiene la informacion conocida por nuestro vehiculo
	 * @param fileNameRecibido nombre del fichero XML que se recibe de otro vehiculo con la informacion que éste conoce
	 * @see manejaXML#leerXML(String)
	 * @return el propio fichero XML del vehiculo (fileNameMiVehiculo) con la informacion del fichero XML fileNameRecibido concatenada.
	 */
	
	public static Document concatenaXML(String fileNameMiVehiculo,String fileNameRecibido)
	{
		try 
		{
			/*
			 * vehiculo.xml es el de mi vehiculo y el que al final se modifica
			 * vehiculoNuevoPrueba.xml es el recibido y del cual tomamos los
			 * datos
			 */

			SAXBuilder builder = new SAXBuilder(false);
			docFinalMio = builder.build(fileNameMiVehiculo);
			docRecibido = builder.build(fileNameRecibido);
			Element raizRecibido = docRecibido.getRootElement();
			Element raizFinalMio = docFinalMio.getRootElement();

			/*
			 * Caso 1): Nuestro vehiculo tiene la <informacion_ajena> vacia (aun
			 * no ha contactado con nadie) y recibe un XML. Posibles casos:
			 * 
			 * 1) XML recibido con <informacion_ajena> vacia (ambos ficheros
			 * iguales: solo <info_propia>
			 * 
			 * 1.1) Añadir la <informacion_propia> del XML recibido a la
			 * <informacion_ajena> de nuestro vehiculo.
			 */

			Element informacion_ajena_mia = raizFinalMio.getChild("informacion_ajena");
			List informacion_propia_recibida = raizRecibido.getChildren("informacion_propia");
			
			/*
			 * En la <info_ajena> de nuestro vehiculo, metemos la <info_propia>
			 * del XML recibido. Mi <info_ajena> esta vacia, asi que simplemente
			 * añadimos un nuevo vehiculo.
			 */

			Iterator it_info_propia_recibida = informacion_propia_recibida.iterator();
			// Usamos este iterador para iterar sobre los atributos del vehiculo
			Element distancia = new Element("distancia");
			Element clima = new Element("clima");
			Element velo = new Element("velocidad");
			Element vmax = new Element("vmax");
			Element dire = new Element("direccion");
			Element acel = new Element("aceleracion");
			Element tiempo = new Element("tiempo");
			while (it_info_propia_recibida.hasNext())
			{
				Element e = (Element) it_info_propia_recibida.next();
				
				distancia.addContent(e.getChildText("distancia"));
				velo.addContent(e.getChildText("velocidad"));
				vmax.addContent(e.getChildText("vmax"));
				acel.addContent(e.getChildText("aceleracion"));
				dire.addContent(e.getChildText("direccion"));
				clima.addContent(e.getChildText("clima"));
				tiempo.addContent(e.getChildText("tiempo"));
			}
				Element vehiculo1 = new Element("vehiculo");
				Attribute nombre_vehiculo_final = raizRecibido.getAttribute("nombre");
				Attribute tipo_vehiculo_final = raizRecibido.getAttribute("tipo");
				vehiculo1.setAttribute("tipo", tipo_vehiculo_final.getValue());
				vehiculo1.setAttribute("nombre", nombre_vehiculo_final.getValue());
				vehiculo1.addContent(distancia);
				vehiculo1.addContent(velo);
				vehiculo1.addContent(vmax);
				vehiculo1.addContent(acel);
				vehiculo1.addContent(dire);
				vehiculo1.addContent(clima);
				vehiculo1.addContent(tiempo);
				informacion_ajena_mia.addContent(vehiculo1);

			/*
			 * Acaba el caso 1.1)
			 */

			/*
			 * Caso 1): Nuestro vehiculo tiene la <informacion_ajena> vacia (aun
			 * no ha contactado con nadie) y recibe un XML. Posibles casos:
			 * 
			 * 2) XML recibido con<informacion_ajena> llena de otros vehiculos.
			 * 
			 * 1.2) Añadir la <informacion_ajena> del XML recibido. Habria que
			 * ver si aparece nuestro vehiculo y filtrarlo. Caso 2):
			 * 
			 * 
			 * Empieza el caso 1.2) -> Concatenar en nuestra <informacion_ajena> la
			 * <informacion_ajena> recibida
			 * 
			 * La idea es ir recorriendo todos los elementos de la <informacion_ajena>
			 * recibida, e ir mirando si existe algun vehiculo que ya esté en el
			 * XML de nuestro vehiculo, en cuyo caso se concatena pero añadiendo
			 * el correspondiente campo de tiempo.
			 */

			Element informacion_ajena_recibida = raizRecibido.getChild("informacion_ajena");
			Iterator it_info_ajena_recibida = informacion_ajena_recibida.getChildren().iterator();
			String nombre_vehiculo_ajeno = null;
			String tipo_vehiculo_ajeno = null;
			
			Element vehiculin = new Element("vehiculo");
			Element distancia1 = new Element("distancia");
			Element clima1 = new Element("clima");
			Element velo1 = new Element("velocidad");
			Element vmax1 = new Element("vmax");
			Element dire1 = new Element("direccion");
			Element acel1 = new Element("aceleracion");
			Element tiempo1 = new Element("tiempo");

			while (it_info_ajena_recibida.hasNext()) 
			{
				/*
				 * Este while nos vale para ver si nuestro vehiculo ya está en a
				 * <informacion_ajena> que recibimos y en tal caso, no añadir la informacion.
				 */
				
				Element e = (Element) it_info_ajena_recibida.next();
				nombre_vehiculo_ajeno = e.getAttribute("nombre").getValue();
				if (!nombre_vehiculo_ajeno.equals(NOM_VEHICULO)) 
				{
					tipo_vehiculo_ajeno = e.getAttribute("tipo").getValue();
					
					if (distancia1 != null) 
					{
						distancia1 = new Element("distancia");
						distancia1.addContent(e.getChildText("distancia"));
					}
					if (velo1 != null) 
					{
						velo1 = new Element("velocidad");
						velo1.addContent(e.getChildText("velocidad"));
					}
					if (vmax1 != null) 
					{
						vmax1 = new Element("vmax");
						vmax1.addContent(e.getChildText("vmax"));
					}
					if (acel1 != null) 
					{
						acel1 = new Element("aceleracion");
						acel1.addContent(e.getChildText("aceleracion"));
					}
					if (dire1 != null) 
					{
						dire1 = new Element("direccion");
						dire1.addContent(e.getChildText("direccion"));
					}
					if (tiempo1 != null) 
					{
						tiempo1 = new Element("tiempo");
						tiempo1.addContent(e.getChildText("tiempo"));
					}
					if (clima1 != null) 
					{
						clima1 = new Element("clima");
						clima1.addContent(e.getChildText("clima"));
					}

					/*
					 * En el caso de que no haya ningun vehiculo en
					 * <informacion_ajena> del XML recibido, nos va a servir
					 * para filtrar y que no de un fallo.
					 */

					vehiculin.setAttribute("tipo", tipo_vehiculo_ajeno);
					vehiculin.setAttribute("nombre", nombre_vehiculo_ajeno);
					
					vehiculin.addContent(distancia1);
					vehiculin.addContent(velo1);
					vehiculin.addContent(vmax1);
					vehiculin.addContent(acel1);
					vehiculin.addContent(dire1);
					vehiculin.addContent(clima1);
					vehiculin.addContent(tiempo1);
					informacion_ajena_mia.addContent(vehiculin);
					vehiculin = new Element("vehiculo");
				}
			}
		}

		/*
		 * Acaba el caso 1.2)
		 */

		catch (Exception e)

		{
			e.printStackTrace();
		}

		XMLOutputter out = new XMLOutputter();
		try 
		{
			FileOutputStream file = new FileOutputStream(fileNameMiVehiculo);
			out.output(docFinalMio, file);
			file.flush();
			file.close();
			out.output(docFinalMio, System.out);
		}

		catch (Exception e)

		{
			e.printStackTrace();
		}

		return docFinalMio;
	}

	/**
	 * Método para limpiar la información contenida en el fichero XML de un vehiculo.
	 * Va a dejar solamente el apartado de <informacion_propia> con la informacion actual del vehiculo,
	 * eliminando todos los vehiculos que aparecen en <informacion_ajena>
	 * @param fileNameMiVehiculo nombre del fichero XML del vehiculo a formatear.
	 */
	public static void ReciclaInformacion(String fileNameMiVehiculo)
	{
		/*
		 * Va reiniciar el fichero XML del vehiculo, eliminando toda informacion ajena y dejando solo
		 * la información propia actual del mismo vehiculo.
		 */
		try 
		{
			SAXBuilder builder = new SAXBuilder(false);
			docFinalMio = builder.build(fileNameMiVehiculo);
			Element raizFinalMio = docFinalMio.getRootElement();
			List informacion_ajena_recibida = raizFinalMio.getChildren("informacion_ajena");
			Iterator it = informacion_ajena_recibida.iterator();
			while (it.hasNext())
			{
				Element e = (Element)it.next();
				it.remove();
			}
			Element info_ajena = new Element("informacion_ajena");
			info_ajena.addContent("");
			raizFinalMio.addContent(info_ajena);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		XMLOutputter out = new XMLOutputter();
		try 
		{
			FileOutputStream file = new FileOutputStream("src/xml/vehiculo.xml");
			out.output(docFinalMio, file);
			file.flush();
			file.close();
			out.output(docFinalMio, System.out);
		}

		catch (IOException e)

		{
			System.err.println("Error al generar el nuevo documento XML");
		}
	}
}
