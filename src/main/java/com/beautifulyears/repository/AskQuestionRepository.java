package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.AskQuestion;

@Repository
public interface AskQuestionRepository extends
		MongoRepository<AskQuestion, Serializable>{

	public List<AskQuestion> findAll();
	
}
