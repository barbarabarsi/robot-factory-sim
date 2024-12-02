package fr.tp.inf112.projects.robotsim.model.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.RemoteFileCanvasChooser;
// import java.util.logging.Logger;

public class RequestProcessor implements Runnable {
    private Socket client;
    
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
	        System.out.println("Connection closes by the client.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (!client.isClosed()) {
	                client.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
} 