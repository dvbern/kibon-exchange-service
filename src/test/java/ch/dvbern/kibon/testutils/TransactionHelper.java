package ch.dvbern.kibon.testutils;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

/**
 * Can be used to execute code in a new transaction.
 */
@Dependent
public class TransactionHelper {

	@Transactional(REQUIRES_NEW)
	@Nullable
	public <T> T newTransaction(@Nonnull Callable<T> c) throws Exception {
		return c.call();
	}

	@Transactional(REQUIRES_NEW)
	public <T> void newTransaction(@Nonnull Runnable r) {
		r.run();
	}
}
