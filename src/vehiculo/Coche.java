package vehiculo;
import java.io.*;
import xml.*;
import conexion.*;
/**
 * Clase principal que modela a nuestro vehiculo. Desde ella se van a crear el servidor y el cliente y se van a iniciar y se llaman al resto de los metodos que van a completar el sistema.
 * @param TAM_PILA tamaño que va a tener la pila donde se van a ir almacenando las alertas. El tamaño depende del numero de tipos distintos de alertas que se vayan a manejar.
 * @param pila Objeto de tipo PilaEventos que será la pila de alertas.
 * @author Manuel Lorenzo
 * @see manejaXML#concatenaXML(String, String)
 * @see Comprobaciones#Comprobaciones(String)
 * @see SimpleClient
 * @see SimpleServer
 */
public class Coche 
{
	/**
	 * Tamaño por defecto de la pila.
	 * Puede ampliarse o reducirse en función del numero distinto de tipos de alertas que vaya a manejar el sistema.
	 */
	public static final int TAM_PILA = 6;
	public static PilaEventos pila;
	public static void main(String[] args) throws IOException
	{
		pila = new PilaEventos(TAM_PILA);
		Thread server = new Thread(new SimpleServer());
		Thread client = new Thread(new SimpleClient());
		server.start();
		client.start();
		
		//El servidor recibe el XML del cliente
		
		manejaXML.concatenaXML("src/xml/vehiculo.xml","src/xml/vehiculoNuevoPrueba.xml");
		// Llama al metodo que concatenar y comprueba los XMLs de la clase que gestiona dichos archivos.
		
		Comprobaciones.Comprobaciones("src/xml/vehiculo.xml");
		//Efectua las comprobaciones de la gestión de la información
		
		pila.ordenaPila(pila);
		pila.muestraAlertas(Coche.pila);
		
		//pila.elements();
		//manejaXML.ReciclaInformacion("src/xml/vehiculo.xml");
		/*
		 * Recicla el XML para que en caso de que esté lleno de información ya inutil,
		 * poder dejarlo sólo con la informacion propia.
		 */
	}
}