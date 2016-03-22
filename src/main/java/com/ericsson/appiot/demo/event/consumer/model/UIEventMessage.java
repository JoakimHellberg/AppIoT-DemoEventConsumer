package com.ericsson.appiot.demo.event.consumer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import se.sigma.sensation.event.sdk.dto.EventMessage;

/**
 * This class is used to present event to UI.
 * @author Joakim Hellberg
 *
 */
@SuppressWarnings("restriction")
public class UIEventMessage {

	private EventMessage source;
	private StringProperty time;
	private StringProperty location;
	private StringProperty device;
	private StringProperty sensor;
	private StringProperty measurement;

	
	public UIEventMessage(EventMessage source) {
		this.source = source;
		setTime(source.getGenerated());
		setLocation(source.getLocationName());
		setDevice(source.getSensorCollectionName());
		setSensor(source.getSensorName());
		setMeasurement(Double.toString(source.getMeasurement().getValues()[0]));
	}
	
	public EventMessage getSource() {
		return source;
	}
	
    public void setTime(String value) { 
    	timeProperty().set(value);
    }
    
    public String getTime() { 
    	return timeProperty().get(); 
    }
    
    public StringProperty timeProperty() { 
        if (time == null) {
        	time = new SimpleStringProperty(this, "time");
        }
        return time; 
    }
	
    public void setLocation(String value) {
    	locationProperty().set(value); 
    }
    
    public String getLocation() { 
    	return locationProperty().get(); 
    }
    
    public StringProperty locationProperty() { 
        if (location == null) {
        	location = new SimpleStringProperty(this, "location");
        }
        return location; 
    }

    public void setDevice(String value) { 
    	deviceProperty().set(value); 
    }
    
    public String getDevice() { 
    	return deviceProperty().get(); 
    }
    
    public StringProperty deviceProperty() { 
        if (device == null) {
        	device = new SimpleStringProperty(this, "device");
        }
        return device; 
    }

    public void setSensor(String value) { 
    	sensorProperty().set(value); 
    }
    
    public String getSensor() { 
    	return sensorProperty().get(); 
    }
    
    public StringProperty sensorProperty() { 
        if (sensor == null) {
        	sensor = new SimpleStringProperty(this, "sensor");
        }
        return device; 
    }
    
    public void setMeasurement(String value) { 
    	measurementProperty().set(value); 
    }
    
    public String getMeasurement() { 
    	return measurementProperty().get(); 
    }
    
    public StringProperty measurementProperty() { 
        if (measurement == null) {
        	measurement = new SimpleStringProperty(this, "measurement");
        }
        return measurement; 
    }   	
}
