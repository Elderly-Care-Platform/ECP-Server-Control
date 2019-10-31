package com.beautifulyears.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.AskCategoryRepository;
import com.beautifulyears.repository.AskQuestionRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.AskCategoryActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.AskQuestionActivityLogHandler;

/**
 * The REST based service for managing "Ask Expert Questions"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/ask" })
public class AskController {
	private static final Logger logger = Logger.getLogger(AskController.class);
	private AskQuestionRepository askQuesRepo;
	private AskCategoryRepository askCatRepo;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<AskQuestion> logHandler;
	ActivityLogHandler<AskCategory> logHandlerCat;

	@Autowired
	public AskController(AskQuestionRepository askQuesRepo, UserRepository userRepository, 
			AskCategoryRepository askCatRepo,
			MongoTemplate mongoTemplate) {
		this.askQuesRepo = askQuesRepo;
		this.askCatRepo = askCatRepo;
		this.mongoTemplate = mongoTemplate;
		logHandler = new AskQuestionActivityLogHandler(mongoTemplate);
		logHandlerCat = new AskCategoryActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the question detail for provided askQuesId
	 * 
	 * @param req
	 * @param askQuesId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getAskQuestionDetail(HttpServletRequest req,
			@RequestParam(value = "askQuesId", required = true) String askQuesId)
			throws Exception {
		LoggerUtil.logEntry();

		AskQuestion askQuestion = askQuesRepo.findOne(askQuesId);
		try {
			if (null == askQuestion) {
				throw new BYException(BYErrorCodes.ASK_QUESTION_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(askQuestion);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitAskQuestion(@RequestBody AskQuestion askQues, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (askQues != null && (Util.isEmpty(askQues.getId()))) {
				
				AskQuestion askQuesExtracted = new AskQuestion(
					askQues.getQuestion(),
					askQues.getDescription(),
					askQues.getAskCategory(),
					askQues.getAskedBy(),
					askQues.getAnsweredBy(),
					askQues.getAnswered()
					);

				askQues = askQuesRepo.save(askQuesExtracted);
				logHandler.addLog(askQues, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new ask question entity created with ID: " + askQues.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askQues);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editAskQuestion(@RequestBody AskQuestion askQues, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (askQues != null && (!Util.isEmpty(askQues.getId()))) {
				AskQuestion oldAskQues = mongoTemplate.findById(new ObjectId(askQues.getId()), AskQuestion.class);
				oldAskQues.setAnswered(askQues.getAnswered());
				oldAskQues.setDescription(askQues.getDescription());
				oldAskQues.setAskCategory(askQues.getAskCategory());
				oldAskQues.setAskedBy(askQues.getAskedBy());
				oldAskQues.setAnsweredBy(askQues.getAnsweredBy());
				oldAskQues.setAnswered(askQues.getAnswered());
				oldAskQues.setQuestion(askQues.getQuestion());

				askQues = askQuesRepo.save(oldAskQues);
				logHandler.addLog(askQues,
						ActivityLogConstants.CRUD_TYPE_UPDATE, request);
				logger.info("old ask question entity updated for ID: "
						+ askQues.getId() + " by User "
						+ currentUser.getId());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askQues);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/list" }, produces = { "application/json" })
	@ResponseBody
	public Object listAll(HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		List<AskQuestion> quesList = null;
		try {
			quesList = askQuesRepo.findAll(new Sort(Direction.DESC, "createdAt"));
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(quesList);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/category/list" }, produces = { "application/json" })
	@ResponseBody
	public Object getCategoryAll(HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		List<AskCategory> qcatList = null;
		try {
			qcatList = askCatRepo.findAll(new Sort(Direction.ASC, "name"));
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(qcatList);
	}

	@RequestMapping(value = { "/category" }, method = { RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	@ResponseBody
	public Object submitCategory(@RequestBody AskCategory askCategory, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (askCategory != null && (Util.isEmpty(askCategory.getId()))) {
					AskCategory askCatExtracted = new AskCategory( askCategory.getName() );
					askCategory = askCatRepo.save(askCatExtracted);
					logHandlerCat.addLog(askCategory, ActivityLogConstants.CRUD_TYPE_CREATE, request);
					logger.info("new ask entity created with ID: " + askCategory.getId());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askCategory);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/category" }, consumes = { "application/json" })
	@ResponseBody
	public Object editCategory(@RequestBody AskCategory askCat, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (askCat != null && (!Util.isEmpty(askCat.getId()))) {
					AskCategory oldAskCat = mongoTemplate.findById(new ObjectId(askCat.getId()), AskCategory.class);
					oldAskCat.setName(askCat.getName());
					
					askCat = askCatRepo.save(oldAskCat);
					logHandlerCat.addLog(askCat,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old ask category entity updated for ID: "
							+ askCat.getId() + " by User "
							+ currentUser.getId());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askCat);
	}
}
