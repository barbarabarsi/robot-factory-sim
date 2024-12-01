import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
// import java.util.logging.Logger;

public class RequestProcessor implements Runnable {
    private Socket client;
    private ServerSocket server;
    private String message;
    
    public RequestProcessor(Socket client, ServerSocket server) {
    	this.client = client;
    	this.server = server;
    }
    
    @Override
    public void run() {
        try {
        	
        	InputStream inpStr = client.getInputStream();
        	Reader strReader = new InputStreamReader(inpStr);
    	    BufferedReader buffReader = new BufferedReader(strReader);
    
    	    message = buffReader.readLine();
            System.out.println("Message received by the server: " + message);
                     
			OutputStream outStr = client.getOutputStream();
			PrintWriter writer = new PrintWriter(outStr, true);
				
			writer.println("Server says: Hello client! Your message was: " + message);
			 
        }
        catch (Exception e) {
        	e.printStackTrace();    
        }        
        finally {
            try {
				if(message == "END") {
					client.close();
					server.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }    
    } 
} 