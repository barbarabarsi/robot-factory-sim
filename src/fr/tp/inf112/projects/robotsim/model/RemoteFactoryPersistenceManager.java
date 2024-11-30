package fr.tp.inf112.projects.robotsim.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;

public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager{

	public RemoteFactoryPersistenceManager(final CanvasChooser canvasChooser) {
		super(canvasChooser);
	}
	
	@Override
	public Canvas read(String canvasId) throws IOException {
	    try (Socket socket = new Socket(InetAddress.getByName("localhost"), 8080);	    		
	         ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
	         ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()))
	    {
	    	outputStream.writeObject(canvasId); 
	        return (Canvas) inputStream.readObject();
	    } 
	    catch (ClassNotFoundException | IOException ex) 
	    {
			throw new IOException(ex);
		}
	}

	@Override
	public void persist(Canvas canvasModel) throws IOException {
	    try (Socket socket = new Socket(InetAddress.getByName("localhost"), 8080);
	         ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()))
	    {
	    	outputStream.writeObject(canvasModel); 
	    }
	    catch (IOException ex) 
	    {
			throw new IOException(ex);
		}
	}


	@Override
	public boolean delete(Canvas canvasModel) throws IOException {
		return false;
	}

}
