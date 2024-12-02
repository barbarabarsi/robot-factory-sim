package fr.tp.inf112.projects.robotsim.model.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	
	public static void main(String args[]){
			
			try( ServerSocket serverSocket = new ServerSocket(80)) {
				System.out.println("Server created: " + serverSocket.getLocalSocketAddress() );	
				do {				
					Socket clientSocket = serverSocket.accept();
					System.out.println("Client conected: " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort() );
					
					Runnable reqProcessor = new RequestProcessor(clientSocket);
					new Thread(reqProcessor).start();
					System.out.println("Started a thread!");
				} while(true);
			}
			catch (IOException ex) { ex.printStackTrace(); }
			
		
					
	}
	

}
