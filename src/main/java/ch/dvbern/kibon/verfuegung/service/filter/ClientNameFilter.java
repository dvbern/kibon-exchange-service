package ch.dvbern.kibon.verfuegung.service.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.clients.model.ClientId_;
import ch.dvbern.kibon.clients.model.Client_;
import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;

public class ClientNameFilter implements Restriction<ClientVerfuegung, ClientVerfuegungDTO> {

	@Nonnull
	private final String clientName;

	@Nullable
	private ParameterExpression<String> clientParam;

	public ClientNameFilter(@Nonnull String clientName) {
		this.clientName = clientName;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientVerfuegung> root, @Nonnull CriteriaBuilder cb) {
		clientParam = cb.parameter(String.class, "clientName");
		Path<String> namePath = root.get(ClientVerfuegung_.client).get(Client_.id).get(ClientId_.clientName);

		return Optional.of(cb.equal(namePath, clientParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientVerfuegungDTO> query) {
		query.setParameter(clientParam, clientName);
	}
}
