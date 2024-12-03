package fr.tp.inf112.projects.robotsim.model.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import fr.tp.inf112.projects.robotsim.model.path.AbstractFactoryPathFinder;

public class Server {

	private static final Logger LOGGER = Logger.getLogger(AbstractFactoryPathFinder.class.getName());
	
	public static void main(String args[]){
			
			try{
				ServerSocket serverSocket = new ServerSocket(80);
				LOGGER.info("Server created: " + serverSocket.getLocalSocketAddress() );	
				do {				
					Socket clientSocket = serverSocket.accept();
					
					Runnable reqProcessor = new RequestProcessor(clientSocket);
					new Thread(reqProcessor).start();
				} while(true);
			}
			catch (IOException e) { 
				LOGGER.severe(e.getMessage());
			}
					
	}
	

}
