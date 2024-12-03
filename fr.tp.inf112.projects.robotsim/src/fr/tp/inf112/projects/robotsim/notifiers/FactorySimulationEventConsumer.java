package fr.tp.inf112.projects.robotsim.notifiers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.tp.inf112.projects.robotsim.app.RemoteSimulatorController;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.utils.SimulationServiceUtils;

public class FactorySimulationEventConsumer {

	private static final Logger LOGGER = Logger.getLogger(FactorySimulationEventConsumer.class.getName());
	private final KafkaConsumer<String, String> consumer;
	private final RemoteSimulatorController controller;

	public FactorySimulationEventConsumer(final RemoteSimulatorController controller) {
		this.controller = controller;
		final Properties props = SimulationServiceUtils.getDefaultConsumerProperties();
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		this.consumer = new KafkaConsumer<>(props);
		final String topicName = SimulationServiceUtils.getTopicName((Factory) controller.getCanvas());
		this.consumer.subscribe(Collections.singletonList(topicName));
	}

	public void consumeMessages() throws JsonMappingException, JsonProcessingException {
		try {
			while (controller.isAnimationRunning()) {
				final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (final ConsumerRecord<String, String> record : records) {
					LOGGER.info("Received JSON Factory text '" + record.value() + "'.");
					controller.setCanvas(SimulationServiceUtils.mapper().readValue(record.value(), Factory.class));
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			consumer.close();
		}
	}

	public void redefineTopic(Factory canvasModel) {
		consumer.unsubscribe();

		final String topicName = SimulationServiceUtils.getTopicName(canvasModel);
		this.consumer.subscribe(Collections.singletonList(topicName));
		LOGGER.info("Consumer Register");
	}
}
