package org.eclipse.scout.apps.egt.shared;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class UiThemeCodeType extends AbstractCodeType<Long, String> {

	private static final long serialVersionUID = 1L;
	public static final Long ID = 110000L;

	public UiThemeCodeType() {
		super();
	}

	@Override
	protected String getConfiguredText() {
		return TEXTS.get("UiTheme");
	}

	@Override
	public Long getId() {
		return ID;
	}

	@Order(10)
	public static class RedCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "red";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Red");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(20)
	public static class BlueCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "blue";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Blue");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(30)
	public static class GreenCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "green";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Green");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(40)
	public static class OrangeCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "orange";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Orange");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(50)
	public static class CyanCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "cyan";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Cyan");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(60)
	public static class GrayCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "gray";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Gray");
		}

		@Override
		public String getId() {
			return ID;
		}
	}
}