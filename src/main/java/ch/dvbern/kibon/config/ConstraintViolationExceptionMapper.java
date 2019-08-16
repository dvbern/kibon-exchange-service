package ch.dvbern.kibon.config;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(@Nonnull final ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(prepareMessage(exception))
			.type(MediaType.TEXT_PLAIN_TYPE)
			.build();
	}

	@Nonnull
	private String prepareMessage(@Nonnull ConstraintViolationException exception) {
		String msg = exception.getConstraintViolations()
			.stream()
			.map(cv -> String.valueOf(cv.getPropertyPath()) + ' ' + cv.getMessage() + '\n')
			.collect(Collectors.joining());

		return msg;
	}
}
