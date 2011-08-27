package pruebas;
import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import org.jdom.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import conexion.SimpleClient;
import conexion.SimpleServer;
import vehiculo.Coche;
import vehiculo.PilaEventos;
import xml.manejaXML;
import xml.xmlSchemaValidatorUtil;

public class PruebasUnitarias
{
	String rutaFichero1="src/xml/vehiculo.xml";
	String rutaFichero2="src/xml/vehiculoNuevoPrueba.xml";
	String rutaEsquemaXML="src/xml/XMLSchema.xsd";
	/**
	 * Comprueba que los ficheros XML se puedan leer sin problemas.
	 */
	@Test
	public void leeXMLs()
	{
		File fichero1 = new File (rutaFichero1);
		File fichero2 = new File (rutaFichero2);
		Assert.assertTrue(fichero1.canRead());
		Assert.assertTrue(fichero2.canRead());
	}
	/**
	 * Se crean dos ficheros XML con ruta incorrecta y se intentan leer.
	 */
	@Test
	public void leeXMLsFALLO()
	{
		try
		{
			File fichero1 = new File ("/rutaFicheroFallo/fichero.xml");
			File fichero2 = new File ("/rutaFicheroFallo/fichero2.xml");
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}
	/**
	 * Concatena dos ficheros XML
	 */
	@Test
	public  void concatenaXML()
	{
		try
		{
			manejaXML.concatenaXML(rutaFichero1, rutaFichero2);
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}
	/**
	 * Prueba el método reciclaXML para comprobar si se borra correctamente la informacion ajena de un fichero XML
	 */
	@Test
	public void reciclaXML()
	{
		try
		{
			manejaXML.ReciclaInformacion(rutaFichero1);
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}
	/**
	 * Comprueba la validación de los documentos XML contra un esquema XML
	 */
	@Test
	public void validaDocumentosXML()
	{
		File XML = new File(rutaFichero1);
		File XMLEsquema = new File(rutaEsquemaXML);
		File XML2 = new File(rutaFichero2);
		try
		{
			xmlSchemaValidatorUtil.validate(XML,XMLEsquema);
			xmlSchemaValidatorUtil.validate(XML2,XMLEsquema);
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}
	/**
	 * Comprueba el fallo al intentar reciclar un fichero XML que no existe
	 */
	@Test
	public void reciclaXMLFALLO()
	{
		try
		{
			manejaXML.ReciclaInformacion("/rutaFicheroFallo/fichero.xml");
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}
	/**
	 * Prueba el establecimiento de conexion en un vehiculo.
	 * @throws IOException
	 */
	@Test
	public void estableceConexion() throws IOException
	{
		SimpleClient c = new SimpleClient();
		SimpleServer s = new SimpleServer();
		c.start();
		s.start();
	}
	/**
	 * Prueba la ordenación de las alertas en la pila.
	 */
	@Test
	public void ordenaPila()
	{
		PilaEventos p = new PilaEventos(Coche.TAM_PILA);
		p.ordenaPila(p);
	}
	/**
	 * Prueba que las alertas en pila se muestren correctamente.
	 */
	@Test
	public void muestraAlertas()
	{
		PilaEventos p = new PilaEventos(Coche.TAM_PILA);
		p.muestraAlertas(p);
	}
}