package com.beautifulyears.rest;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.EventRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.EventActivityLogHandler;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The REST based service for managing "event"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/event" })
public class EventController {
	private static final Logger logger = Logger
			.getLogger(EventController.class);
	private EventRepository eventRepository;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Event> logHandler;
	
	@Autowired
	public EventController(EventRepository eventRepository, 
			MongoTemplate mongoTemplate) {
		this.eventRepository = eventRepository;
		this.mongoTemplate = mongoTemplate;
		logHandler = new EventActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the event detail for provided eventId
	 * 
	 * @param req
	 * @param eventId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getEventDetail(HttpServletRequest req,
			@RequestParam(value = "eventId", required = true) String eventId)
			throws Exception {
		LoggerUtil.logEntry();
		
		Event event = eventRepository.findOne(eventId);
		try {
			if (null == event) {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitEvent(@RequestBody Event event, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (event != null && (Util.isEmpty(event.getId()))) {
				event.setOrganiser(currentUser.getUserName());
				Event eventExtracted = new Event(
					event.getTitle(), 
					event.getDatetime(), 
					event.getDescription(),
					event.getCapacity(), 
					event.getEntryFee(), 
					event.getEventType(), 
					event.getStatus(), 
					event.getAddress(), 
					event.getLandmark(), 
					event.getLanguages(), 
					event.getOrganiser(), 
					event.getOrgPhone(), 
					event.getOrgEmail());

				event = eventRepository.save(eventExtracted);
				logHandler.addLog(event, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new event entity created with ID: " + event.getId() + " for Organiser " + event.getOrganiser());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editEvent(@RequestBody Event event, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (event != null && (!Util.isEmpty(event.getId()))) {
				Event oldEvent = mongoTemplate.findById(new ObjectId(event.getId()), Event.class);
				oldEvent.setTitle(event.getTitle()); 
				oldEvent.setDatetime(event.getDatetime());
				oldEvent.setDescription(event.getDescription());
				oldEvent.setCapacity(event.getCapacity());
				oldEvent.setEntryFee(event.getEntryFee());
				oldEvent.setEventType(event.getEventType());
				oldEvent.setStatus(event.getStatus());
				oldEvent.setAddress(event.getAddress());
				oldEvent.setLandmark(event.getLandmark());
				oldEvent.setLanguages(event.getLanguages());
				oldEvent.setOrganiser(event.getOrganiser()); 
				oldEvent.setOrgPhone(event.getOrgPhone()); 
				oldEvent.setOrgEmail(event.getOrgEmail());
				oldEvent.setLastModifiedAt(new Date());
				
				event = eventRepository.save(oldEvent);
				logHandler.addLog(event,
						ActivityLogConstants.CRUD_TYPE_UPDATE, request);
				logger.info("new event entity created with ID: "
						+ event.getId() + " by User "
						+ event.getOrganiser());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/list" }, produces = { "application/json" })
	@ResponseBody
	public Object allEvents(
			@RequestParam(value = "startDatetime", required = false) Long startDatetime,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		
		List<Event> eventList = eventRepository.findAll(new Sort(
			Direction.DESC, "createdAt"));
		return BYGenericResponseHandler.getResponse(eventList);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object deleteEvent(@PathVariable("eventId") String eventId,
			HttpServletRequest request) throws Exception {
		@SuppressWarnings("unused")
		User currentUser = Util.getSessionUser(request);
		Event event = eventRepository.findOne(eventId);
		if (null != event) {
			System.out.println("Inside DELETE");
			eventRepository.delete(eventId);
			logHandler.addLog(event, ActivityLogConstants.CRUD_TYPE_DELETE, request);
		} else {
			throw new Exception();
		}
		return BYGenericResponseHandler.getResponse(null);
	}
}
