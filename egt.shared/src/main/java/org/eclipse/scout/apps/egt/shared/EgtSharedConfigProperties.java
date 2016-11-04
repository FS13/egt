package org.eclipse.scout.apps.egt.shared;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

public final class EgtSharedConfigProperties {

	public static class EgtApplicationLanguageProperty extends AbstractStringConfigProperty {

		@Override
		public String getKey() {
			return "egt.application.language";
		}

	}

}
