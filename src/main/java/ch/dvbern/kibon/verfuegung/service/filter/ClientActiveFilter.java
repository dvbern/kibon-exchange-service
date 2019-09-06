package ch.dvbern.kibon.verfuegung.service.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;

public class ClientActiveFilter implements Restriction<ClientVerfuegung, ClientVerfuegungDTO> {

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientVerfuegung> root, @Nonnull CriteriaBuilder cb) {
		return Optional.of(cb.isTrue(root.get(ClientVerfuegung_.active)));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientVerfuegungDTO> query) {
		// nop
	}
}
