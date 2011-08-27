package xml;

import java.io.File;
import org.w3c.dom.Document;

/**
 * Aplicación para validar los XML con un esquema
 * 
 * @author Carlos García. Autentia.
 * @see http://www.mobiletest.es
 */

public class validadorXML 
{
	public static void main(String[] args) 
	{
		String xmlFilePath = "C:/Users/Manolo/Workspace/Proyecto/src/xml/vehiculoNuevoPrueba.xml";
		String schemaFilePath = "C:/Users/Manolo/Workspace/Proyecto/src/xml/XMLSchema.xsd";
		try 
		{
			File xml = new File(xmlFilePath);
			File schema = new File(schemaFilePath);
			Document document = xmlSchemaValidatorUtil.validate(xml, schema);

			System.out.println("El documento está bien formado y es válido");
			System.out.println(document.getFirstChild().getNodeName());
		}
		catch (Exception ex) 
		{
			System.out.println(ex);
		}
	}
}