package fr.tp.inf112.projects.robotsim.notifiers;

import java.util.LinkedList;
import java.util.List;

import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.robotsim.model.Notifier;

public class LocalFactoryModelChangedNotifier extends Notifier {
	
	private List<Observer> observers = new LinkedList<Observer>();
	
	public List<Observer> getObservers(){
		return observers;
	}
	
	@Override
	public void notifyObservers() {
		
		for (Observer observer : observers) {
			observer.modelChanged();
		}
	}

	@Override
	public boolean addObserver(Observer observer) {
		return observers.add(observer);
	}

	@Override
	public boolean removeObserver(Observer observer) {
		return observers.remove(observer);
	}
}