package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.robotsim.notifiers.FactoryModelChangedNotifier;

public class Notifier implements FactoryModelChangedNotifier{
	
	@JsonIgnore
	private transient List<Observer> observers;
	
	public Notifier() {
		this(null);
	}
	
	public Notifier(List<Observer> observers) {
		this.observers = observers;
	}
	
	public List<Observer> getObservers() {
		if (observers == null) {
			observers = new ArrayList<>();
		}
		
		return observers;
	}
	
	@Override
	public void notifyObservers() {
		
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
