package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "askquestion")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AskQuestion {

	@Id
	private String 	id;
	private String 	question;
	private String 	description;

	@DBRef
	private AskCategory askCategory;

	@DBRef
	private User	askedBy;

	@DBRef
	private UserProfile	answeredBy;
	
	private Boolean	answered;
	private final Date createdAt = new Date();
	private Date lastModifiedAt = new Date();

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public UserProfile getAnsweredBy() {
		return answeredBy;
	}

	public void setAnsweredBy(UserProfile answeredBy) {
		this.answeredBy = answeredBy;
	}

	public Boolean getAnswered() {
		return answered;
	}

	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}
	
	public User getAskedBy() {
		return askedBy;
	}

	public void setAskedBy(User askedBy) {
		this.askedBy = askedBy;
	}

	public AskCategory getAskCategory() {
		return askCategory;
	}

	public void setAskCategory(AskCategory askCategory) {
		this.askCategory = askCategory;
	}
	
	public AskQuestion() {
	}

	public AskQuestion(String question, String description, AskCategory askCategory, User askedBy, UserProfile answeredBy, Boolean answered) {
		this.question = question;
		this.description = description;
		this.askCategory = askCategory;
		this.askedBy = askedBy;
		this.answeredBy = answeredBy;
		this.answered = answered;
	}
}
