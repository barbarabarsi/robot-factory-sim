package fr.tp.inf112.projects.robotsim.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.managers.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.notifiers.FactorySimulationEventConsumer;
import fr.tp.inf112.projects.robotsim.notifiers.LocalFactoryModelChangedNotifier;
import fr.tp.inf112.projects.robotsim.utils.SimulationServiceUtils;

public class RemoteSimulatorController extends SimulatorController{
	
	private static final Logger LOGGER = Logger.getLogger(RemoteSimulatorController.class.getName());
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private HttpClient httpClient;
	private HttpRequest request;
	private URI uri;
	private HttpResponse<String> response;
	private ObjectMapper objectMapper;
	
	public RemoteSimulatorController(Factory factory, RemoteFactoryPersistenceManager persistenceManager) throws URISyntaxException, IOException, InterruptedException {
		super(factory, persistenceManager);
		httpClient = HttpClient.newHttpClient(); 
		objectMapper = SimulationServiceUtils.mapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void startAnimation() {
		
		try {			
			final URI uri = new URI("http", null, "localhost", 8080, "/start/" + factoryModel.getId(), null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			LOGGER.info("" + request);
			
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			LOGGER.info("" + response);
			
			ObjectMapper objectMapper = new ObjectMapper();
			boolean value = objectMapper.readValue(response.body(), boolean.class);
			isRunning.set(true);
			
			if (value) {
				FactorySimulationEventConsumer consumer = new FactorySimulationEventConsumer(this);
				
				new Thread( () -> {
					try {
						consumer.consumeMessages();
					} catch (JsonProcessingException e) {
						LOGGER.severe(e.getMessage());
					}
				}).start();
			}


		} catch (Exception e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}

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
			
			isRunning.set(false);
			
		} catch (URISyntaxException | InterruptedException | IOException e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
    	this.factoryModel.setSimulationStatus(false);
    }
	
    @Override 
    public boolean isAnimationRunning() {
    	return isRunning.get();
    }
   
	
	private Factory getFactory() {
		Factory factory = null;
		try {			
			final URI uri = new URI("http", null, "localhost", 8080, "/get/" + factoryModel.getId(), null, null);
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() == 404) {
				return null;
			}
			factory = objectMapper.readValue(response.body(), Factory.class);

		} catch (InterruptedException | IOException | URISyntaxException e) {
			LOGGER.severe(e.getMessage() + e.getStackTrace());
		}
		return factory;
	}
	
	@Override
	public void setCanvas(final Canvas canvasModel) {
		
		if (factoryModel == null) {
			this.factoryModel = (Factory) canvasModel;
		}
		else {
			if (factoryModel.getId() != null && canvasModel.getId() != null && factoryModel.getId() != canvasModel.getId()) {
				
				isRunning.set(false);
				stopAnimation();
				
				FactorySimulationEventConsumer consumer = new FactorySimulationEventConsumer(this);
				factoryModel.setId(canvasModel.getId());
				
				consumer.redefineTopic((Factory) canvasModel);
			}
			LocalFactoryModelChangedNotifier notifier = (LocalFactoryModelChangedNotifier) factoryModel.getNotifier();
			factoryModel = (Factory) canvasModel;		
			factoryModel.setNotifier(notifier);
			factoryModel.notifyObservers();
		}
		
	}
	
}
