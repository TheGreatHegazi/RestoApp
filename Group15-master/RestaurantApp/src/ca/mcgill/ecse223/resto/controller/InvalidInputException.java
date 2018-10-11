package ca.mcgill.ecse223.resto.controller;

// Taken from BTMS Application tutorial
public class InvalidInputException extends Exception {
	
	// Serial Version UID Defined but InvalidInputException never serialized
	private static final long serialVersionUID = -5633915762703837868L;
	
	public InvalidInputException(String errorMessage) {
		super(errorMessage);
	}

}