package com.ericsson.appiot.demo.event.consumer;

import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.appiot.demo.event.consumer.model.UIEventMessage;

import javafx.collections.ObservableList;
import se.sigma.sensation.event.sdk.EventManager;
import se.sigma.sensation.event.sdk.EventMessageListener;
import se.sigma.sensation.event.sdk.OutboxException;
import se.sigma.sensation.event.sdk.dto.EventMessage;
import se.sigma.sensation.event.sdk.dto.EventResetMessage;
import se.sigma.sensation.event.sdk.dto.EventStatus;
import se.sigma.sensation.event.sdk.dto.IntegrationTicket;

@SuppressWarnings("restriction")
public class MyEventProcessor implements EventMessageListener {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private EventManager eventManager;
	private ObservableList<UIEventMessage> eventList;
	public MyEventProcessor(IntegrationTicket integrationTicket, ObservableList<UIEventMessage> eventList) {
		eventManager = new EventManager(integrationTicket);
		this.eventList = eventList;
	}	

	public void start() {
		eventManager.setEventMessageListener(this);
		eventManager.startInbox();
	}
	
	public void stop() {
		eventManager.stopInbox();
	}
	
	public void onEventMessage(EventMessage eventMessage) {
		if(eventMessage.getEventStatus() == EventStatus.Active) {
			UIEventMessage message = new UIEventMessage(eventMessage);
			eventList.add(message);
		} else {
			EventMessage removeMe = null;
			ListIterator<UIEventMessage> existingEvents = eventList.listIterator();
			while(existingEvents.hasNext()) {
				UIEventMessage existing = existingEvents.next();
				EventMessage existingSource = existing.getSource();
				if(existingSource.getId().equals(eventMessage.getId())) {
					removeMe = existing.getSource(); 
				}
			}
			if(removeMe != null) {
				eventList.remove(removeMe);
			}
		}
	}

	public void reset(int index) {		
		EventResetMessage resetMessage = new EventResetMessage();
		EventMessage eventMessage = eventList.get(index).getSource();
		resetMessage.setEventId(eventMessage.getId());
		try {
			eventManager.sendEventMessageAcknowledgement(resetMessage);
			eventList.remove(index);
		} catch (OutboxException e) {
			logger.log(Level.SEVERE, "Could not send reset message.", e);
		}		
	}
}
