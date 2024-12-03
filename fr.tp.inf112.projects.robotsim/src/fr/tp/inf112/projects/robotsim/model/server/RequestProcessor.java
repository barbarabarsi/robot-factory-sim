package fr.tp.inf112.projects.robotsim.model.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;
import fr.tp.inf112.projects.robotsim.managers.FactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.managers.RemoteFileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class RequestProcessor implements Runnable {
    private Socket client;
    private static final Logger LOGGER = Logger.getLogger(RequestProcessor.class.getName());
    
    public RequestProcessor(Socket client) {
    	this.client = client;
    }
    
    @Override
    public void run() {
        try {
        	BufferedInputStream buffInputStream = new BufferedInputStream(client.getInputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(buffInputStream);
            Object readObject = objectInputStream.readObject();
            
            BufferedOutputStream buffOutputStream = new BufferedOutputStream(client.getOutputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffOutputStream);
           
            FactoryPersistenceManager persistenceManager = new FactoryPersistenceManager(new RemoteFileCanvasChooser("factory", "Puck Factory"));
            
            if (readObject instanceof String && "BROWSER".equals(readObject)) 
            {
	        	File currentDirectory = new File(".");
	        	String[] fileNames = currentDirectory.list((dir, name) -> name.endsWith(".factory"));
                
	        	if (fileNames != null) 
		    		objectOutputStream.writeObject(fileNames);               	
				else {
					objectOutputStream.writeObject(new String[0]);
				}
				objectOutputStream.flush();
            }
            else if (readObject instanceof String) 
            {
                Canvas factory =  persistenceManager.read((String) readObject);
                if (factory == null) {
					factory = SimulatorApplication.defaultFactory();
					factory.setId((String) readObject);
					persistenceManager.persist((Canvas) factory);
				}
                objectOutputStream.writeObject(factory); 
                objectOutputStream.flush();
                
            } 
            else if (readObject instanceof Factory) 
            {
            	persistenceManager.persist((Canvas) readObject);
            }
			 
	    } catch (SocketException e) {
	        LOGGER.info("Connection closed by the client.");
	    } catch (Exception e) {
	    	LOGGER.severe(e.getMessage());
	    } finally {
	        try {
	            if (!client.isClosed()) {
	                client.close();
	            }
	        } catch (IOException e) {
	        	LOGGER.severe(e.getMessage());
	        }
	    }
	}
} 