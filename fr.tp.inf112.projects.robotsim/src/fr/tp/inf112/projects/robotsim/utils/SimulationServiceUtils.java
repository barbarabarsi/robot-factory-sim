package fr.tp.inf112.projects.robotsim.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class SimulationServiceUtils {
	public static final String BOOTSTRAP_SERVERS = "localhost:9092";
	private static final String GROUP_ID = "Factory-Simulation-Group";
	private static final String AUTO_OFFSET_RESET = "latest";
	private static final String TOPIC = "simulation-";
	
	public static ObjectMapper mapper() { 
		final PolymorphicTypeValidator typeValidator =  BasicPolymorphicTypeValidator.builder() 
				.allowIfSubType(PositionedShape.class.getPackageName()) 
				.allowIfSubType(Component.class.getPackageName()) 
				.allowIfSubType(BasicVertex.class.getPackageName()) 
				.allowIfSubType(ArrayList.class.getName()) 
				.allowIfSubType(LinkedHashSet.class.getName()) 
				.build(); 
		ObjectMapper objectMapper = new ObjectMapper(); 
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL); 
	
		return objectMapper;
	}
	
	public static String getTopicName(final Factory factoryModel) {
		return TOPIC + factoryModel.getId();
	}
	
	public static Properties getDefaultConsumerProperties() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);
		return props;     
		}
	}