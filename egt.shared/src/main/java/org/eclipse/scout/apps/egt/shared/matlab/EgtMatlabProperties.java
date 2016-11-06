package org.eclipse.scout.apps.egt.shared.matlab;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

public class EgtMatlabProperties {

	public static class EgtMatlabInstallationLocationProperty extends AbstractStringConfigProperty {

		@Override
		public String getKey() {
			return "egt.matlab.installation.location";
		}

	}

	public static class EgtMatlabFilesLocationProperty extends AbstractStringConfigProperty {

		@Override
		public String getKey() {
			return "egt.matlab.files.location";
		}

	}

}
