package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.canvas.model.impl.RGBColor;
import fr.tp.inf112.projects.robotsim.model.motion.Motion;
import fr.tp.inf112.projects.robotsim.model.path.FactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.shapes.CircularShape;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Robot extends Component {
	
	private static final long serialVersionUID = -1218857231970296747L;

	private static final Style STYLE = new ComponentStyle(RGBColor.GREEN, RGBColor.BLACK, 3.0f, null);

	private static final Style BLOCKED_STYLE = new ComponentStyle(RGBColor.RED, RGBColor.BLACK, 3.0f, new float[]{4.0f});

	private final Battery battery;
	
	private int speed;
	
	private List<Component> targetComponents;
	
	@JsonIgnore 
	private transient Iterator<Component> targetComponentsIterator;
	
	private Component currTargetComponent;
	
	@JsonIgnore 
	private transient Iterator<Position> currentPathPositionsIter;
	
	@JsonIgnore 
	private transient boolean blocked;
	
	private Position nextPosition;
	
	private FactoryPathFinder pathFinder;
	
	public Robot() {
		this(null, null, null, null, null);
	}

	public Robot(final Factory factory,
				 final FactoryPathFinder pathFinder,
				 final CircularShape shape,
				 final Battery battery,
				 final String name ) {
		super(factory, shape, name);
		
		this.pathFinder = pathFinder;
		
		this.battery = battery;
		
		targetComponents = new ArrayList<>();
		currTargetComponent = null;
		currentPathPositionsIter = null;
		speed = 5;
		blocked = false;
		nextPosition = null;
	}

	@Override
	public String toString() {
		return super.toString() + " battery=" + battery + "]";
	}

	@JsonInclude
	protected int getSpeed() {
		return speed;
	}

	protected void setSpeed(final int speed) {
		this.speed = speed;
	}
	
	private List<Component> getTargetComponents() {
		if (targetComponents == null) {
			targetComponents = new ArrayList<>();
		}
		
		return targetComponents;
	}
	
	public boolean addTargetComponent(final Component targetComponent) {
		return getTargetComponents().add(targetComponent);
	}
	
	public boolean removeTargetComponent(final Component targetComponent) {
		return getTargetComponents().remove(targetComponent);
	}
	
	@Override
	@JsonInclude
	public boolean isMobile() {
		return true;
	}

	@Override
	public boolean behave() {
		if (getTargetComponents().isEmpty()) {
			return false;
		}
		
		if (currTargetComponent == null || hasReachedCurrentTarget()) {
			currTargetComponent = nextTargetComponentToVisit();
		}
		
		computePathToCurrentTargetComponent();

		return moveToNextPathPosition() != 0;
	}
		
	private Component nextTargetComponentToVisit() {
		if (targetComponentsIterator == null || !targetComponentsIterator.hasNext()) {
			targetComponentsIterator = getTargetComponents().iterator();
		}
		
		return targetComponentsIterator.hasNext() ? targetComponentsIterator.next() : null;
	}
	
	private Position getNextPosition() {
		return this.nextPosition;
	}
	
	private PositionedShape getPositionShape(Position position) { 
		return new RectangularShape(position.getxCoordinate(), position.getyCoordinate(), 2, 2);
	}
	
	@Override 
	public boolean isLivelyLocked() {
		final Position nextPosition = getNextPosition();
		if (nextPosition == null) {
			return false; 
		}
		final Robot otherRobot = (Robot) getFactory().getMobileComponentAt(getPositionShape(nextPosition), this);
		return otherRobot != null && getPosition().equals(otherRobot.getNextPosition()); 
	} 
	
	private int moveToNextPathPosition() { 
		final Motion motion = computeMotion();
		int displacement = motion == null ? 0 : this.getFactory().moveComponent(motion, this);
		
		 if (displacement != 0) {
			 notifyObservers(); 
		 } 
		 else if (isLivelyLocked()) { 
			 final Position freeNeighbouringPosition = findFreeNeighbouringPosition();
			 if (freeNeighbouringPosition != null) {
				 nextPosition = freeNeighbouringPosition;
				 displacement = moveToNextPathPosition();
				 computePathToCurrentTargetComponent(); 
			 } 
		 } 
		 return displacement;
	} 
	
	private Position findFreeNeighbouringPosition() {
		int[] currentPosition = {getPositionedShape().getPosition().getxCoordinate(), getPositionedShape().getPosition().getyCoordinate()};
		int[][] alternatives = {{0, 5}, {0, -5}, {5, 0}, {-5, 0}};
		
		for(int i = 0; i<=4; i+=1) {
	        int newX = currentPosition[0] + alternatives[i][0];
	        int newY = currentPosition[1] + alternatives[i][1];
	        
	        PositionedShape shape = new RectangularShape(newX, newY, 2, 2);

	        if (!getFactory().hasMobileComponentAt(shape, this) && 
	            !getFactory().hasObstacleAt(shape)) {
	            return shape.getPosition();
	        }
		}
		return null;
	
	}
	
	private void computePathToCurrentTargetComponent() {
		final List<Position> currentPathPositions = pathFinder.findPath(this, currTargetComponent);
		currentPathPositionsIter = currentPathPositions.iterator();
	}
	
	private Motion computeMotion() {
		if (!currentPathPositionsIter.hasNext()) {

			// There is no free path to the target
			blocked = true;
			
			return null;
		}
		
		final Position nextPosition = this.nextPosition == null ? currentPathPositionsIter.next() : this.nextPosition;
		final PositionedShape shape = new RectangularShape(nextPosition.getxCoordinate(),
				   										   nextPosition.getyCoordinate(),
				   										   2,
				   										   2);
		if (getFactory().hasMobileComponentAt(shape, this)) {
			this.nextPosition = nextPosition;
			
			return null;
		}

		this.nextPosition = null;
		
		return new Motion(getPosition(), nextPosition);
	}
	
	private boolean hasReachedCurrentTarget() {
		return getPositionedShape().overlays(currTargetComponent.getPositionedShape());
	}
	
	@Override
	@JsonInclude
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
	
	@Override
	@JsonInclude
	public Style getStyle() {
		return blocked ? BLOCKED_STYLE : STYLE;
	}
}
