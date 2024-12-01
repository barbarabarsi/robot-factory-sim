package fr.tp.slr201.projects.robotsim.service.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.RemoteFileCanvasChooser;

@SpringBootApplication
@RestController
public class RemoteSimulationController {

	private static final Logger LOGGER = Logger.getLogger(RemoteSimulationController.class.getName());

    private List<Factory> factoryModelList;
    private RemoteFactoryPersistenceManager persistenceManager;
    
    public RemoteSimulationController() {
    	this.factoryModelList = new ArrayList<Factory>();
    	persistenceManager = new RemoteFactoryPersistenceManager(new RemoteFileCanvasChooser("factory", "Puck Factory"));
    }


    @GetMapping("/start/{factoryId}")
    public boolean startSimulation(@PathVariable String factoryId) {
        LOGGER.info("Request received to start simulation for factory ID: " + factoryId);
        
		try {
			Factory factory = (Factory) persistenceManager.read(factoryId);
			factoryModelList.add(factory);
			factory.startSimulation();
			
			LOGGER.info("Simulation started successfully for factory ID:" + factoryId);
            return true;
		} catch (IOException e) {
			LOGGER.fine("Failed to start simulation for factory ID: " + factoryId);
            return false;
		}
    }

    @GetMapping("/get/{factoryId}")
    public Factory getFactory(@PathVariable String factoryId) {
    	LOGGER.info("Request received to retrieve simulation for factory ID: " + factoryId);
    	Factory factory = findFactory(factoryId);
    	
        if (factory != null) {
        	LOGGER.info("Factory model retrieved successfully for factory ID: " + factoryId);
            return factory;
        } else {
        	LOGGER.fine("No simulation found for factory ID: {}" + factoryId);
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
    	for(Factory fac : factoryModelList) {
    		if (fac.getId() == factoryId)
    			return fac;
    	}
    	return null;
    }
}