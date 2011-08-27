package conexion;
import java.net.*;
import java.io.*;

import vehiculo.Coche;
public class SimpleServer extends Thread 
{
	/**
	 * Clase que modela el servidor de un vehiculo.
	 * Crea un nuevo socket en un puerto determinado donde se van a conectar los clientes y va a quedar a la espera de conexiones.
	 * En cuanto reciba conexiones, envia el fichero XML y al finalizar, cierra la conexion.
	 * @see Coche
	 * @see SimpleClient
	 */
	int in;
	public void run() 
	{
		/**
		 * El metodo run va a permitir que un vehiculo tenga varios servidores que puedan conectar con otros vehiculos, funcionando de manera independiente.
		 */
		try 
		{
			System.out.println("Ejecutando servidor del vehiculo ");
			ServerSocket sock = new ServerSocket(50000);

			// now listening for connections

			while (true) 
			{
				Socket client = sock.accept();
				try {
					byte[] receivedData = new byte[8192];
					BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("vehiculoNuevoPrueba.xml"));
					while ((in = bis.read(receivedData)) != -1) 
					{
						bos.write(receivedData, 0, in);
					}
					bos.close();
					System.out.println("XML enviado con exito");
				} catch (Exception e) 
				{
					System.err.println("Error: no se ha podido enviar la información");
				}
				/*
				 * Cierra el socket y reanuda las conexiones de escucha
				 */
				client.close();
			}
		} catch (IOException e) 
		{
			System.err.println(e);
		}
	}
}