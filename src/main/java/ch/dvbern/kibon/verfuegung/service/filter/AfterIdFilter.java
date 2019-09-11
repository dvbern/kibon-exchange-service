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

/**
 * Utility class for filtering criteria queries to only deliver entries with an ID > the specified one.
 */
public class AfterIdFilter implements Restriction<ClientVerfuegung, ClientVerfuegungDTO> {

	@Nullable
	private final Long afterId;

	@Nullable
	private ParameterExpression<Long> param;

	public AfterIdFilter(@Nullable Long afterId) {
		this.afterId = afterId;
	}

	@Override
	@Nonnull
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientVerfuegung> root, @Nonnull CriteriaBuilder cb) {
		if (afterId == null) {
			return Optional.empty();
		}

		param = cb.parameter(Long.class, "id");

		return Optional.of(cb.greaterThan(root.get(ClientVerfuegung_.id), param));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientVerfuegungDTO> query) {
		if (afterId == null) {
			return;
		}

		query.setParameter(param, afterId);
	}
}
