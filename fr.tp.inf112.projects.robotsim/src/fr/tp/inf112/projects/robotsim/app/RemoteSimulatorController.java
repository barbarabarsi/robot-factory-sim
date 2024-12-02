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

import com.fasterxml.jackson.core.JsonProcessingException;
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
	
	public RemoteSimulatorController(Factory factory, RemoteFactoryPersistenceManager persistenceManager) throws URISyntaxException, IOException, InterruptedException {
		super(factory, persistenceManager);
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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void startAnimation() {
		
		try {			
			final URI uri = new URI("http", null, "localhost", 8080, "/start/" + factoryModel.getId(), null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			ObjectMapper objectMapper = new ObjectMapper();
			Boolean value = objectMapper.readValue(response.body(), boolean.class);
			System.out.println(value);
			
		} catch (URISyntaxException | InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
		run();
		
    }
	
	/**
	 * {@inheritDoc}
	 */
    @Override
    public void stopAnimation() {
    	try {			
			final URI uri = new URI("http", null, "localhost", 8080, "/stop/" + factoryModel.getId(), null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			objectMapper.readValue(response.body(), boolean.class);
			
		} catch (URISyntaxException | InterruptedException | IOException e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
    	run();
    }
	
	private void updateViewer() throws InterruptedException, URISyntaxException, IOException {
		final URI uri = new URI("http", null, "localhost", 8080, "/get/" + factoryModel.getId(), null, null);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println( response);
		final Factory remoteFactoryModel = objectMapper.readValue(response.body(), Factory.class);
	
		while (((Factory) getCanvas()).isSimulationStarted()) {	
			
			for(Observer ob : factoryModel.getObservers()) {
				remoteFactoryModel.addObserver(ob);
			}	
			this.factoryModel = remoteFactoryModel;

			setCanvas(remoteFactoryModel); 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
	}
	
	private void run() {
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
		final List<Observer> observers = factoryModel.getObservers();
		super.setCanvas(canvasModel);
		
		for (final Observer observer : observers) {
			((Factory) canvasModel).addObserver(observer);
		}
		((Component) canvasModel).notifyObservers();
		
		factoryModel = (Factory) canvasModel;
	}
	
}
