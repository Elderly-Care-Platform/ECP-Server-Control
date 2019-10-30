package com.beautifulyears.rest;

import java.util.Date;
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
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.ProductRepository;
import com.beautifulyears.repository.ProductCategoryRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ProductActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ProductCategoryActivityLogHandler;

/**
 * The REST based service for managing "product"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/product" })
public class ProductController {
	private static final Logger logger = Logger
			.getLogger(ProductController.class);
	private ProductRepository productRepository;
	private ProductCategoryRepository productCatRepo;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Product> logHandler;
	ActivityLogHandler<ProductCategory> logHandlerCat;

	@Autowired
	public ProductController(ProductRepository productRepository, UserRepository userRepository, 
			ProductCategoryRepository productCatRepo,
			MongoTemplate mongoTemplate) {
		this.productRepository = productRepository;
		this.productCatRepo = productCatRepo;
		this.mongoTemplate = mongoTemplate;
		logHandler = new ProductActivityLogHandler(mongoTemplate);
		logHandlerCat = new ProductCategoryActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the product detail for provided productId
	 * 
	 * @param req
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getProductDetail(HttpServletRequest req,
			@RequestParam(value = "productId", required = true) String productId)
			throws Exception {
		LoggerUtil.logEntry();
		
		Product product = productRepository.findOne(productId);
		try {
			if (null == product) {
				throw new BYException(BYErrorCodes.PRODUCT_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitProduct(@RequestBody Product product, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		if (product != null && (Util.isEmpty(product.getId()))) {
			
			Product productExtracted = new Product(
				product.getName(), 
				product.getProductCategory(),
				product.getShortDescription(),
				product.getDescription(),
				product.getIsFeatured(),
				product.getRating(),
				product.getReviews(),
				product.getPrice(), 
				product.getStatus(),
				product.getBuyLink(),
				product.getBuyFrom(),
				product.getImages()
				);

			product = productRepository.save(productExtracted);
			logHandler.addLog(product, ActivityLogConstants.CRUD_TYPE_CREATE, request);
			logger.info("new product entity created with ID: " + product.getId());
		} else {
			throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
		}
		
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editProduct(@RequestBody Product product, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (product != null && (!Util.isEmpty(product.getId()))) {
				Product oldProduct = mongoTemplate.findById(new ObjectId(product.getId()), Product.class);
				oldProduct.setName(product.getName());
				oldProduct.setProductCategory(product.getProductCategory());
				oldProduct.setShortDescription(product.getShortDescription());
				oldProduct.setDescription(product.getDescription());
				oldProduct.setIsFeatured(product.getIsFeatured());
				oldProduct.setRating(product.getRating());
				oldProduct.setReviews(product.getReviews());
				oldProduct.setPrice(product.getPrice());
				oldProduct.setStatus(product.getStatus());
				oldProduct.setBuyLink(product.getBuyLink());
				oldProduct.setBuyFrom(product.getBuyFrom());
				oldProduct.setImages(product.getImages());
				oldProduct.setLastModifiedAt(new Date());
				
				product = productRepository.save(oldProduct);
				logHandler.addLog(product,
						ActivityLogConstants.CRUD_TYPE_UPDATE, request);
				logger.info("old product entity updated for ID: "
						+ product.getId() + " by User "
						+ currentUser.getId());
				
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/list" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(HttpServletRequest request) throws Exception {
		List<Product> producttList = null;
		LoggerUtil.logEntry();
		try {
			producttList = productRepository.findAll(new Sort(
			Direction.DESC, "createdAt"));
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(producttList);
	}	

	@RequestMapping(method = { RequestMethod.GET }, value = { "/category/list" }, produces = { "application/json" })
	@ResponseBody
	public Object getCategoryPage(HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		List<ProductCategory> productCatList = null;
		try {
			productCatList = productCatRepo.findAll(new Sort(
				Direction.ASC, "name"));
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productCatList);
	}

	@RequestMapping(value = { "/category" }, method = { RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	@ResponseBody
	public Object submitProductCategory(@RequestBody ProductCategory productCategory, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (productCategory != null && (Util.isEmpty(productCategory.getId()))) {
				ProductCategory productCatExtracted = new ProductCategory(
				productCategory.getName()
				);

				productCategory = productCatRepo.save(productCatExtracted);
				logHandlerCat.addLog(productCategory, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new product entity created with ID: " + productCategory.getId());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(productCategory);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/category" }, consumes = { "application/json" })
	@ResponseBody
	public Object editProductCategory(@RequestBody ProductCategory productCat, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (productCat != null && (!Util.isEmpty(productCat.getId()))) {
				ProductCategory oldProductCat = mongoTemplate.findById(new ObjectId(productCat.getId()), ProductCategory.class);
				oldProductCat.setName(productCat.getName());
				
				productCat = productCatRepo.save(oldProductCat);
				logHandlerCat.addLog(productCat,
						ActivityLogConstants.CRUD_TYPE_UPDATE, request);
				logger.info("old product category entity updated for ID: "
						+ productCat.getId() + " by User "
						+ currentUser.getId());
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(productCat);
	}	
}
