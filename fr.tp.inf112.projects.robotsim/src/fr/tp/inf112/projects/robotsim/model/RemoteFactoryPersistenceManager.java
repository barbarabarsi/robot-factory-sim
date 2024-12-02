package fr.tp.inf112.projects.robotsim.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
	    try (Socket socket = new Socket(InetAddress.getByName("localhost"), 80))
	    {
	    	BufferedOutputStream buffOutputStream = new BufferedOutputStream(socket.getOutputStream());
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffOutputStream);
	        objectOutputStream.writeObject(canvasId); 
	    	objectOutputStream.flush();
	    	
	    	BufferedInputStream buffInputStream = new BufferedInputStream(socket.getInputStream());
	        ObjectInputStream objectinputStream = new ObjectInputStream(buffInputStream);
	        return (Canvas) objectinputStream.readObject();
	    } 
	    catch (ClassNotFoundException | IOException ex) 
	    {
			throw new IOException(ex);
		}
	}

	@Override
	public void persist(Canvas canvasModel) throws IOException {
	    try (Socket socket = new Socket(InetAddress.getByName("localhost"), 80);
    		 BufferedOutputStream buffOutputStream = new BufferedOutputStream(socket.getOutputStream());
	         ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffOutputStream))
	    {
	    	objectOutputStream.writeObject(canvasModel); 
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