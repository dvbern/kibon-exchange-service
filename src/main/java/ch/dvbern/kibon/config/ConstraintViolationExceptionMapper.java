/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
