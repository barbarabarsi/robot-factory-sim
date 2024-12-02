package fr.tp.inf112.projects.robotsim.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Machine extends Component {

	private static final long serialVersionUID = -1568908860712776436L;

	public Machine() {
		this(null, null, null);
	}
	
	public Machine(final Area area,
				   final RectangularShape shape,
				   final String name) {
		super(area.getFactory(), shape, name);
		
		area.setMachine(this);
	}

	@Override
	public String toString() {
		return super.toString() + "]";
	}
	
	@Override
	@JsonInclude
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
}
