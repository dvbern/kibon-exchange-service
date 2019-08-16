package ch.dvbern.kibon.persistence;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface Restriction<X, Y> {

	@Nonnull
	Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb);

	void setParameter(@Nonnull TypedQuery<Y> query);
}
