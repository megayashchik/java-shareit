package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFoundException(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDuplicateEmailException(final DuplicateEmailException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(NotOwnerException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleNotOwnerException(final NotOwnerException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleThrowable(final Throwable e) {
		return new ErrorResponse("Произошла непредвиденная ошибка." + e.getMessage());
	}
}