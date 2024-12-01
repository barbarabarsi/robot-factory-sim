import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	
	public static void main(String args[]){
		
		do {
			try( ServerSocket serverSocket = new ServerSocket(8080)) {
				System.out.println("Server created: " + serverSocket.getLocalSocketAddress() );	
				
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client conected: " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort() );
				
				Runnable reqProcessor = new RequestProcessor(clientSocket, serverSocket);
				new Thread(reqProcessor).start();
				System.out.println("Started a thread!");
			}
			catch (IOException ex) { ex.printStackTrace(); }
			
		} while(true);
					
	}
	

}
