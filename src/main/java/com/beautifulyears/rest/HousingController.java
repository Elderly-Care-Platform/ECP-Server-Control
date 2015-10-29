/**
 * 
 */
package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.repository.HousingRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.HousingResponse;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.util.LoggerUtil;

/**
 * @author Nitin
 *
 */

@Controller
@RequestMapping("/housing")
public class HousingController {
	private static Logger logger = Logger.getLogger(HousingController.class);
	private static HousingRepository staticHousingRepository;
	private HousingRepository housingRepository;
	// private static final Logger logger =
	// Logger.getLogger(HousingController.class);

	@Autowired
	public HousingController(HousingRepository housingRepository,
			MongoTemplate mongoTemplate) {
		this.housingRepository = housingRepository;
		staticHousingRepository = housingRepository;
	}

	/*@RequestMapping(method = RequestMethod.GET, value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object allDiscuss() {
		List<HousingFacility> housingList = housingRepository.findAll(new Sort(
				Sort.Direction.DESC, "createdAt"));
		return BYGenericResponseHandler.getResponse(housingList);
	}*/
	
	@RequestMapping(method = RequestMethod.GET, value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object allDiscuss(
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "tags", required = false) List<String> tags,
			HttpServletRequest request) throws Exception {

		HousingResponse.HousingPage housingPage = null;
		PageImpl<HousingFacility> page = null;
		
		Direction sortDirection = Direction.DESC;
	
		String temp = "null";
		List<ObjectId> tagIds = new ArrayList<ObjectId>();
		if(temp.equals(city)){
			city = null;
		}
		if(temp.equals(tags)){
			tags = null;
		}
		if (null != tags) {
			for (String tagId : tags) {
				tagIds.add(new ObjectId(tagId));
			}
		}
		Pageable pageable = new PageRequest(0, 100, sortDirection, "createdAt");
		page = staticHousingRepository.getPage(city, tagIds, null, null, null, pageable);
		housingPage = HousingResponse.getPage(page, null);
		return BYGenericResponseHandler.getResponse(housingPage);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/list/cities", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object allCities() {
		List<HousingFacility> housingList = housingRepository.findAll();
		Set<String> cityList = new HashSet<String> ();
		for(HousingFacility hf: housingList){
			String cities = hf.getPrimaryAddress().getCity();
			cityList.add(cities);
		}
		return BYGenericResponseHandler.getResponse(cityList);
	}	

	@RequestMapping(method = { RequestMethod.GET }, value = { "/{housingId}" }, produces = { "application/json" })
	@ResponseBody
	public Object getHousingbyID(
			@PathVariable(value = "housingId") String housingId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		HousingFacility housingFacility = null;
		try {
			if (housingId != null) {
				housingFacility = this.housingRepository.findById(housingId);
			} else {
				logger.error("invalid parameter");
			}

		} catch (Exception e) {
			logger.error("invalid parameter");
		}
		return BYGenericResponseHandler.getResponse(housingFacility);
	}

	/* @PathVariable(value = "userId") String userId */
	@RequestMapping(method = { RequestMethod.PUT }, value = { "/{housingId}" }, consumes = { "application/json" })
	@ResponseBody
	public Object updateHousing(@RequestBody HousingFacility housingFacility,
			@PathVariable(value = "housingId") String housingId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		LoggerUtil.logEntry();
		HousingFacility facility = null;
		try {
			if ((housingFacility != null) && (housingId != null)) {
				facility = housingRepository.findById(housingId);

				if (facility != null) {
					facility.setStatus(housingFacility.getStatus());
					facility.setFeatured(housingFacility.isFeatured());
					facility.setVerified(housingFacility.isVerified());
					housingRepository.save(facility);
					logger.info("User Profile update with details: "
							+ facility.toString());
				}

			}
		} catch (Exception e) {
			logger.error("error ");
		}

		return BYGenericResponseHandler.getResponse(facility);
	}

}
