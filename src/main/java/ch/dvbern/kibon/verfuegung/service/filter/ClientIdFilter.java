package ch.dvbern.kibon.verfuegung.service.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;

public class ClientIdFilter implements Restriction<ClientVerfuegung, ClientVerfuegungDTO> {

	@Nonnull
	private final String clientId;

	@Nullable
	private ParameterExpression<String> clientParam;

	public ClientIdFilter(@Nonnull String clientId) {
		this.clientId = clientId;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientVerfuegung> root, @Nonnull CriteriaBuilder cb) {
		clientParam = cb.parameter(String.class, "clientId");

		return Optional.of(cb.equal(root.get(ClientVerfuegung_.clientId), clientParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientVerfuegungDTO> query) {
		query.setParameter(clientParam, clientId);
	}
}
