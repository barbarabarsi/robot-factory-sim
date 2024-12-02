package fr.tp.inf112.projects.robotsim.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class RemoteSimulatorController extends SimulatorController{
	
	private static final Logger LOGGER = Logger.getLogger(RemoteSimulatorController.class.getName());
	
	private HttpClient httpClient;
	private HttpRequest request;
	private URI uri;
	private HttpResponse<String> response;
	private ObjectMapper objectMapper;
	private Factory factory;
	
	public RemoteSimulatorController(Factory factory, RemoteFactoryPersistenceManager persistenceManager) throws URISyntaxException, IOException, InterruptedException {
		super(factory, persistenceManager);
		this.factory = factory;
		httpClient = HttpClient.newHttpClient(); 
		
		final PolymorphicTypeValidator typeValidator =  BasicPolymorphicTypeValidator.builder() 
				.allowIfSubType(PositionedShape.class.getPackageName()) 
				.allowIfSubType(Component.class.getPackageName()) 
				.allowIfSubType(BasicVertex.class.getPackageName()) 
				.allowIfSubType(ArrayList.class.getName()) 
				.allowIfSubType(LinkedHashSet.class.getName()) 
				.build(); 
		objectMapper = new ObjectMapper(); 
		objectMapper.activateDefaultTyping(typeValidator,  ObjectMapper.DefaultTyping.NON_FINAL); 
		
		runUpdate();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void startAnimation() {
		try {			
			if(factory.getId() == null) {
				factory.setId(UUID.randomUUID().toString());
			}
			uri = new URI("http", null, "localhost", 8080, "/start/" + factory.getId() , null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			objectMapper.readValue(response.body(), boolean.class);
				
		} catch ( IOException | InterruptedException | URISyntaxException e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
    }
	
	/**
	 * {@inheritDoc}
	 */
    @Override
    public void stopAnimation() {
    	try {			
			uri = new URI("http", null, "localhost", 8080, "/stop/" + factory.getId() , null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			objectMapper.readValue(response.body(), boolean.class);
			
    	} catch ( IOException | InterruptedException | URISyntaxException e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
    }
	
	private void updateViewer() throws InterruptedException, URISyntaxException, IOException {
		while (((Factory) getCanvas()).isSimulationStarted()) {
			final Factory remoteFactoryModel = getFactory();
			setCanvas(remoteFactoryModel); 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public Factory getFactory() throws URISyntaxException {

		try {
			uri = new URI("http", null, "localhost", 8080, "/get/" + factory.getId() , null, null);
			
			request = HttpRequest.newBuilder().uri(uri).GET().build();
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			LOGGER.info(response.toString());
			
			return objectMapper.readValue(response.body(), Factory.class);
		} 
		catch (URISyntaxException | IOException | InterruptedException e) {
			LOGGER.warning(e.getMessage());
			
			return null;
		}
	}
	
	private void runUpdate() {
		new Thread(() -> {
			try {
				this.updateViewer();
			} catch (InterruptedException | URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void setCanvas(final Canvas canvasModel) {
		
        if (getCanvas() == null) {
            LOGGER.warning("Current canvas is null. Initializing with a new Factory instance.");
            super.setCanvas(new Factory(200, 200, "Default Factory"));
            
        }
        
		final List<Observer> observers = ((Factory) getCanvas()).getObservers();
		super.setCanvas(canvasModel);

		for (final Observer observer : observers) {
			((Factory) getCanvas()).addObserver(observer); 
		} 
		((Factory) getCanvas()).notifyObservers(); 
	} 
	
}
