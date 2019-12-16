/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;

import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.User;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Nitin
 *
 */
public class EventActivityLogHandler extends ActivityLogHandler<Event> {

	public EventActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(Event event, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (event != null) {
			log.setActivityTime(new Date());
			int eventType = event.getEventType();
			
			log.setActivityType(eventType);
			log.setCrudType(crudType);
			log.setDetails("ADMIN: event id = " + event.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(event.getId());
			log.setRead(false);
			log.setTitleToDisplay(event.getTitle());
			if (null != currentUser) {
				log.setUserId(currentUser.getId());
			}
		}
		return log;
	}
}
