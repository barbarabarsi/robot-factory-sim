package fr.tp.slr201.projects.robotsim.service.simulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import fr.tp.inf112.projects.robotsim.managers.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.managers.RemoteFileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.notifiers.FactoryModelChangedNotifier;
import fr.tp.inf112.projects.robotsim.notifiers.LocalFactoryModelChangedNotifier;

@RestController
public class MicroServiceController {

	private static final Logger LOGGER = Logger.getLogger(MicroServiceController.class.getName());

    private List<Factory> factoryModelList = new ArrayList<Factory>();
    private RemoteFactoryPersistenceManager persistenceManager = new RemoteFactoryPersistenceManager(new RemoteFileCanvasChooser("factory", "Puck Factory"));;
   
    @Autowired
	private KafkaTemplate<String, Factory> simulationEventTemplate;

    @GetMapping("/start/{factoryId}")
    public boolean startSimulation(@PathVariable String factoryId) {
        LOGGER.info("Request received to start simulation for factory ID: " + factoryId);
        
		try 
		{
			Factory factory = (Factory) persistenceManager.read(factoryId);
			factoryModelList.add(factory);
			LOGGER.info("JUJUBA " + factory);
			final FactoryModelChangedNotifier notifier = new KafkaFactoryModelChangeNotifier(factory, simulationEventTemplate);
	        factory.setNotifier(notifier);
	
			new Thread(() -> factory.startSimulation()).start();
			LOGGER.info("Simulation started successfully for factory ID:" + factoryId);
            return true;
		} 
		catch (Exception e) 
		{
			LOGGER.severe(e.getMessage());
			LOGGER.severe("Failed to start simulation for factory ID: " + factoryId);
            return false;
		}
    }

    @GetMapping("/get/{factoryId}")
    public Factory getFactory(@PathVariable String factoryId) {
    	
    	LOGGER.info("Request received to retrieve simulation for factory ID: " + factoryId);
    	Factory factory = findFactory(factoryId);
        
    	if (factory != null) 
    	{
        	LOGGER.info("Factory model retrieved successfully for factory ID: " + factoryId);
            return factory;
        } else 
        {
        	LOGGER.info("No simulation found for factory ID: " + factoryId);
            return null;
        }
    }

    @GetMapping("/stop/{factoryId}")
    public boolean stopSimulation(@PathVariable String factoryId) {
    	LOGGER.info("Request received to stop simulation for factory ID: " + factoryId);
    	
		Factory factory = findFactory(factoryId);
		factory.stopSimulation();
		LOGGER.info("Simulation stopped successfully for factory ID: " + factoryId);
		return true;
    }
    
    public Factory findFactory(String factoryId) {
    	for(Factory fac : factoryModelList) 
    	{
 			if(new File(fac.getId()).getName() == factoryId)
 				return fac;
    	}	
    	try 
		{
			return (Factory) persistenceManager.read(factoryId);
		} 
		catch (Exception e)
		{
			LOGGER.severe(e.getMessage());
		}
		return null;
    }
}