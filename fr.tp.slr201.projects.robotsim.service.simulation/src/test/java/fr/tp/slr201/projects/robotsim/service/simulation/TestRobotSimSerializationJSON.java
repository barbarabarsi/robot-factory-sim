package fr.tp.slr201.projects.robotsim.service.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

@SpringBootTest

public class TestRobotSimSerializationJSON {
	private final ObjectMapper objectMapper; 
	private static final Logger LOGGER = Logger.getLogger(TestRobotSimSerializationJSON.class.getName());
	
	public TestRobotSimSerializationJSON() { 
		objectMapper = new ObjectMapper();
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder() 
		.allowIfSubType(PositionedShape.class.getPackageName()) 
		.allowIfSubType(Component.class.getPackageName()) 
		.allowIfSubType(BasicVertex.class.getPackageName()) 
		.allowIfSubType(ArrayList.class.getName()) 
		.allowIfSubType(LinkedHashSet.class.getName()) 
		.build();
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
	}
	
	@Test 
	public void testSerialization() throws JsonProcessingException {
		
		var factory = SimulatorApplication.defaultFactory();
		
		final String factoryAsJsonString = objectMapper.writeValueAsString(factory);
		LOGGER.info(factoryAsJsonString);
		final Factory roundTrip = objectMapper.readValue(factoryAsJsonString, Factory.class);
		
		assertEquals(factoryAsJsonString, objectMapper.writeValueAsString(roundTrip));
	} 
}
