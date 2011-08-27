package conexion;
import java.net.*;
import java.io.*;

import vehiculo.Coche;
/**
 * Clase que modela el cliente de un vehiculo.
 * Se va a conectar a un socket en una direccion IP determinada por una constante y un puerto y va a recibir un fichero XML.
 * @see Coche
 * @see SimpleServer
 * @author Manuel Lorenzo
 */
public class SimpleClient extends Thread 
{
	int in;
	final String IP="127.0.0.1";
	public void run() 
	{
		/**
		 * El metodo va a permitir que la clase Cliente pueda conectar a varios servidores de otros vehiculos al mismo tiempo y de manera independiente.
		 */
		try {
			System.out.println("Ejecutando cliente del vehiculo ");
			// Conecta a un socket servidor
			Socket sock = new Socket(IP, 50000);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream("vehiculo.xml"));
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			byte[] byteArray = new byte[8192];

			while ((in = bis.read(byteArray)) != -1) 
			{
				bos.write(byteArray, 0, in);
			}
			bis.close();
			bos.close();
			bos.flush();
			System.out.println("XML recibido con exito");
		} 
		catch (IOException e) 
		{
			System.err.println(e);

		}
	}
}