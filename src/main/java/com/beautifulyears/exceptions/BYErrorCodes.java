/**
 * 
 */
package com.beautifulyears.exceptions;

/**
 * @author Nitin
 *
 */
public enum BYErrorCodes {
	// generic errors
	INVALID_REQUEST(1001, "The request is invalid"),
	MISSING_PARAMETER(1002, "Required query parameter is missing"),
	INTERNAL_SERVER_ERROR(1003, "Some unknown internal server error occured"),
	ERROR_IN_SENDING_MAIL(1004,"Some unexpected error occured while sending the mail"),
	NO_CONTENT_FOUND(1005,"No content was found with queries criteria"),

	// discuss related error
	DISCUSS_NOT_FOUND(2001, "Discuss content with provided discussId doesn't exist"),
	DISCUSS_ALREADY_LIKED_BY_USER(2002, "Discuss content already liked by the logged in user"),

	// user error
	USER_NOT_AUTHORIZED(3001, "User is not authorized to perform the selected operation"),
	USER_LOGIN_REQUIRED(3002, "User is required to login to perform such operation"),
	USER_LOGIN_FAILED(3003,"User login failed. Invalid user/password combination."),
	USER_ALREADY_EXIST(3004,"User with the same credentials already exists"),
	INVALID_SESSION(3005,"Invalid session, please login to continue"),
	USER_EMAIL_DOES_NOT_EXIST(3006,"User's emailId is not registered"),
	USER_CODE_EXPIRED(3007,"Validation code has been expired, please generate a new one"),
	USER_CODE_DOES_NOT_EXIST(3008,"Validation code entered is invalid. Please enter a valid code."),
	
	//review rate
	REVIEW_TYPE_INVALID(4001,"Invalid review type"),
	RATING_VALUE_INVALID(4002,"Invalid rating value,rating percentage value should be between 0 to 100."),
	
	//profile error
	USER_PROFILE_DOES_NOT_EXIST(5001,"User profile for the sent userId does not exist")

	;

	private final int id;
	private final String msg;

	BYErrorCodes(int id, String msg) {
		this.id = id;
		this.msg = msg;
	}

	public int getId() {
		return this.id;
	}

	public String getMsg() {
		return this.msg;
	}
}
