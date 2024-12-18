package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.tp.inf112.projects.canvas.controller.Observable;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.Figure;
import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.robotsim.model.motion.Motion;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;
import fr.tp.inf112.projects.robotsim.notifiers.FactoryModelChangedNotifier;
import fr.tp.inf112.projects.robotsim.notifiers.LocalFactoryModelChangedNotifier;

public class Factory extends Component implements Canvas, Observable {

	private static final Logger LOGGER = Logger.getLogger(Factory.class.getName());

	private static final long serialVersionUID = 5156526483612458192L;
	
	private static final ComponentStyle DEFAULT = new ComponentStyle(5.0f);

	@JsonManagedReference
    private List<Component> components;

	@JsonIgnore
	public transient boolean simulationStarted;
	
	@JsonIgnore
	private transient FactoryModelChangedNotifier  notifier;
	
	public Factory() {}

	public Factory(final int width,
				   final int height,
				   final String name ) {
		super(null, new RectangularShape(0, 0, width, height), name);
		
		components = new ArrayList<>();
		simulationStarted = false;
		this.notifier = new LocalFactoryModelChangedNotifier();
	}
	

	@Override
	public boolean addObserver(Observer observer) {
		if (notifier != null) {
			return notifier.addObserver(observer);
		}
		return false;
		// return getObservers().add(observer);
	}

	@Override
	public boolean removeObserver(Observer observer) {
		if (notifier != null) {
			return notifier.removeObserver(observer);
		}
		return false;
	}
	
	public void notifyObservers() {
		if (notifier != null) {
			notifier.notifyObservers();
		}
//		for (final Observer observer : getObservers()) {
//			observer.modelChanged();
//		}
	}
	
	public boolean addComponent(final Component component) {
		if (components.add(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	public boolean removeComponent(final Component component) {
		if (components.remove(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	public List<Component> getComponents() {
		return components;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@JsonIgnore
	public Collection<Figure> getFigures() {
		return (Collection) components;
	}

	@Override
	public String toString() {
		return super.toString() + " components=" + components + "]";
	}
	
	public boolean isSimulationStarted() {
		return simulationStarted;
	}
	
	@JsonIgnore
	public List<Observer> getObservers() {
		return getNotifier().getObservers();
	}
	
	public void setSimulationStatus(boolean status) {
		simulationStarted = status;
	}

	public void startSimulation() {

		if (!isSimulationStarted()) {
			this.simulationStarted = true;
			notifyObservers();

			behave();
		}
	}

	public void stopSimulation() {
		if (isSimulationStarted()) {
			this.simulationStarted = false;
			
			notifyObservers();
		}
	}
	
	public FactoryModelChangedNotifier getNotifier() {
		return notifier;
	}
	
	public void setNotifier(FactoryModelChangedNotifier notifier) {
		this.notifier = notifier;
	}


	@Override
	public boolean behave() {
		boolean behaved = true;
		
		for (final Component component : getComponents()) {
			Thread componentThread = new Thread(component);
			componentThread.start();
		}
		
		return behaved;
	}
	
	@Override
	@JsonIgnore
	public Style getStyle() {
		return DEFAULT;
	}
	
	@JsonIgnore
	public boolean hasObstacleAt(final PositionedShape shape) {
		for (final Component component : getComponents()) {
			if (component.overlays(shape) && !component.canBeOverlayed(shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	@JsonIgnore
	public boolean hasMobileComponentAt(final PositionedShape shape,
										final Component movingComponent) {
		for (final Component component : getComponents()) {
			if (component != movingComponent && component.isMobile() && component.overlays(shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Component getMobileComponentAt(final PositionedShape shape, final Component movingComponent) {
		for (final Component component : getComponents()) {
			if (component != movingComponent && component.isMobile() && component.overlays(shape)) {
				return component;
			}
		}
		return null;
	}
	
	protected synchronized int moveComponent(final Motion motion, final Component componentToMove) {
		int xCoordinate = motion.getTargetPosition().getxCoordinate();
		int yCoordinate = motion.getTargetPosition().getyCoordinate();
		
		final PositionedShape destiny = new RectangularShape(xCoordinate, yCoordinate, 2, 2);
		
		if (this.hasMobileComponentAt(destiny, componentToMove) || this.hasObstacleAt(destiny)) {
			return 0;
		}
	
		return motion.moveToTarget();
	}


}
