package fr.tp.inf112.projects.robotsim.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class ChargingStation extends Component {
	
	private static final long serialVersionUID = -154228412357092561L;
	
	private boolean charging;
	
	public ChargingStation() {
		this((Factory) null, null, null);
	}

	public ChargingStation(final Room room,
						   final RectangularShape shape,
						   final String name) {
		this(room.getFactory(), shape, name);
	}

	public ChargingStation(final Factory factory,
						   final RectangularShape shape,
						   final String name) {
		super(factory, shape, name);
		
		charging = false;
	}

	@Override
	public String toString() {
		return super.toString() + "]";
	}

	@JsonInclude
	protected boolean isCharging() {
		return charging;
	}

	protected void setCharging(boolean charging) {
		this.charging = charging;
	}

	@JsonInclude
	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
}
