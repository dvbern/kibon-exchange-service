package ch.dvbern.kibon.testutils;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class EasyMockExtension implements TestInstancePostProcessor {

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
		EasyMockSupport.injectMocks(testInstance);
	}
}
