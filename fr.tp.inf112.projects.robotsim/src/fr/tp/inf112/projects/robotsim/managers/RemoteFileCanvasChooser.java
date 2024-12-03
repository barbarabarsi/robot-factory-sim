package fr.tp.inf112.projects.robotsim.managers;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;

public class RemoteFileCanvasChooser extends FileCanvasChooser{

    // public RemoteFileCanvasChooser() {}
	
	private static final Logger LOGGER = Logger.getLogger(RemoteFileCanvasChooser.class.getName());
    
    public RemoteFileCanvasChooser(final String fileExtension, final String documentTypeLabel) {
		this(null, fileExtension, documentTypeLabel);
	}

	public RemoteFileCanvasChooser(Component viewer, String fileExtension, String documentTypeLabel) {
		super(viewer, fileExtension, documentTypeLabel);
		if (fileExtension == null) {
			throw new IllegalArgumentException("File extension cannot be null.");
		}
	}
	
	@Override
	public String browseCanvases(boolean open) throws IOException{
	    
        try{   	
        	if (open) {
        		try (Socket socket = new Socket(InetAddress.getByName("localhost"), 80)) {
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
						
					outputStream.writeObject("BROWSER");

					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					String[] files = (String[]) inputStream.readObject();
         
					if (files.length == 0) {
						JOptionPane.showMessageDialog(null, "There are no files to load.");
						return null;
					}
					String selectedFile = (String) JOptionPane.showInputDialog(
					        null,
					        "Select a file to open:",
					        "Open File",
					        JOptionPane.PLAIN_MESSAGE,
					        null,
					        files,
					        files[0]
					);
					
					
					if (selectedFile != null && !selectedFile.trim().isEmpty()) {
						File file = new File("./" + selectedFile);
					    if (file.exists()) {
					    	return file.getPath();
					    }
					}
				} catch (HeadlessException e) {
					LOGGER.severe(e.getMessage());
				}
        		
           	 	return null;
	        } 
        	else {
    	        String name = JOptionPane.showInputDialog("Enter file name to save:", ".factory");
    	        File file = new File("./" + name);
    	        return file.getPath();
    	    }
	       
	    } 
	    catch (ClassNotFoundException | IOException e) 
        {
	    	LOGGER.severe(e.getMessage());
		}
	  
	    
	    return null;
	}


}
