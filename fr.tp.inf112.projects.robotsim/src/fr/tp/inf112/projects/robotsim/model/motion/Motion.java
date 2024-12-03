package fr.tp.inf112.projects.robotsim.model.motion;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.tp.inf112.projects.robotsim.model.Position;

public class Motion {
	
	private final Position currentPosition;

	private final Position targetPosition;
	
	public Motion() {
		this(null, null);
	}


	public Motion(final Position currentPosition,
				  final Position targetPosition) {
		this.currentPosition = currentPosition;
		this.targetPosition = targetPosition;
	}

	@JsonInclude
	public Position getCurrentPosition() {
		return currentPosition;
	}

	@JsonInclude
	public Position getTargetPosition() {
		return targetPosition;
	}
	
	public int moveToTarget() {
		final int xDisplacement = Math.abs(targetPosition.getxCoordinate() - currentPosition.getxCoordinate());
		final int yDisplacement = Math.abs(targetPosition.getyCoordinate() - currentPosition.getyCoordinate());
		final int displacement = (int) Math.round(Math.sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement));
		
		currentPosition.setxCoordinate(targetPosition.getxCoordinate());
		currentPosition.setyCoordinate(targetPosition.getyCoordinate());
		
		return displacement;
	}
}
