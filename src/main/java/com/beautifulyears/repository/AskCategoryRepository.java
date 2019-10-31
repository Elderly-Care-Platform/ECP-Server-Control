package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.AskCategory;

@Repository
public interface AskCategoryRepository extends
		MongoRepository<AskCategory, Serializable> {

	public List<AskCategory> findAll();
	
}
