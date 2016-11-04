
package org.eclipse.scout.apps.egt.shared.file;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

public final class EgtGraphFileConfigProperties {

	public static class EgtGraphsFileProperty extends AbstractStringConfigProperty {

		@Override
		public String getKey() {
			return "egt.file.graphs.file";
		}

	}

	public static class EgtGraphsDirectoryProperty extends AbstractStringConfigProperty {

		@Override
		public String getKey() {
			return "egt.file.graphs.directory";
		}

	}

}
