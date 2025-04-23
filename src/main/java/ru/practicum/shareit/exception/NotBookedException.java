package ru.practicum.shareit.exception;

public class NotBookedException extends RuntimeException{
	public NotBookedException(String message) {
		super(message);
	}
}