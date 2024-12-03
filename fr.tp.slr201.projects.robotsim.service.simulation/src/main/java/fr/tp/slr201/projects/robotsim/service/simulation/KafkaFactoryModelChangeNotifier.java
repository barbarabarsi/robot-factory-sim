package fr.tp.slr201.projects.robotsim.service.simulation;


import java.util.concurrent.CompletableFuture;

import org.springframework.messaging.Message;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;

import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.Notifier;

public class KafkaFactoryModelChangeNotifier extends Notifier {

	private KafkaTemplate<String, Factory> simulationEventTemplate; 
	private Factory factory;
	
	public KafkaFactoryModelChangeNotifier(Factory factory, KafkaTemplate<String, Factory> simulationEventTemplate){
		this.factory = factory;
		this.simulationEventTemplate = simulationEventTemplate;
		TopicBuilder.name("simulation-" + factory.getId()).build(); 
	}
	
	@Override
	public void notifyObservers() {
		final Message<Factory> factoryMessage = MessageBuilder.withPayload(factory).setHeader(KafkaHeaders.TOPIC, "simulation-" + factory.getId()).build();
		
		final CompletableFuture<SendResult<String, Factory>> sendResult = simulationEventTemplate.send(factoryMessage);
		sendResult.whenComplete((result, ex) -> { if (ex != null) { throw new RuntimeException(ex); } }); 
	}

	@Override
	public boolean addObserver(Observer observer) {
		return false;
	}

	@Override
	public boolean removeObserver(Observer observer) {
		return false;
	}

}
