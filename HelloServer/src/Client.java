import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public Socket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;
    private BufferedReader buffReader;
    public int id;

    
    public static void main(String args[]){

    	try(Socket clientSocket = new Socket( InetAddress.getByName("localhost"), 8080);){

    		System.out.println("Client starting request...");
    		
    		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            
    		writer.println("Client says: Hello World!");
            
            listenServer(clientSocket);
            
            writer.println("END");

    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
       
    }
    
    public static void listenServer(Socket client) {
    	try{
    		InputStream inpStr = client.getInputStream();
        	Reader strReader = new InputStreamReader(inpStr);
        	BufferedReader buffReader = new BufferedReader(strReader);
    		
    		String message = buffReader.readLine();
            System.out.println("Message sended by the server for client : '"+ message +"'");
            
            buffReader.close();
   
    	}
    	catch(IOException ex) {
    		ex.printStackTrace();
    	}
       
    }
    
	public void stop() {
		try {
			clientSocket.close();
		} catch (IOException e) { e.printStackTrace(); }
	}


}
