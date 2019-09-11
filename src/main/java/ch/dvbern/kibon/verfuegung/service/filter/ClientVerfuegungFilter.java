package ch.dvbern.kibon.verfuegung.service.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;

/**
 * Helper class for filtering {@link ClientVerfuegung}en.
 */
public class ClientVerfuegungFilter {

	@Nullable
	private final Integer limit;
	@Nonnull
	private final List<Restriction<ClientVerfuegung, ClientVerfuegungDTO>> restrictions = new ArrayList<>();

	/**
	 * For filtering by clientName only.
	 */
	public ClientVerfuegungFilter(@Nonnull String clientName) {
		this(clientName, null, null);
	}

	/**
	 * @param clientName the clients name
	 * @param afterId for pagination, the id after which results are wanted
	 * @param limit max. amount of results
	 */
	public ClientVerfuegungFilter(
		@Nonnull String clientName,
		@Nullable Long afterId,
		@Nullable Integer limit) {

		restrictions.add(new ClientActiveFilter());
		restrictions.add(new ClientNameFilter(clientName));
		restrictions.add(new AfterIdFilter(afterId));

		this.limit = limit;
	}

	/**
	 * Sets the filter predicates on the given query.
	 */
	public void setPredicate(
		@Nonnull CriteriaQuery<ClientVerfuegungDTO> query,
		@Nonnull Root<ClientVerfuegung> root,
		@Nonnull CriteriaBuilder cb) {

		Predicate[] predicates = restrictions.stream()
			.map(r -> r.getPredicate(root, cb))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toArray(Predicate[]::new);

		query.where(predicates);
	}

	/**
	 * Sets the filter parameters on the given query.
	 */
	public void setParameters(@Nonnull TypedQuery<ClientVerfuegungDTO> query) {
		restrictions.forEach(r -> r.setParameter(query));

		if (limit != null) {
			query.setMaxResults(limit);
		}
	}
}
