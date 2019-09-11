package ch.dvbern.kibon.persistence;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Utility wrapper for filtering criteria queries.
 */
public interface Restriction<X, Y> {

	/**
	 * Creates the predicate.
	 */
	@Nonnull
	Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb);

	/**
	 * Sets the parameter onto the query.
	 */
	void setParameter(@Nonnull TypedQuery<Y> query);
}
