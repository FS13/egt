package org.eclipse.scout.apps.egt.shared.graph;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

public class EgtSpeciesCodeType extends AbstractCodeType<Long, Long> {

	private static final long serialVersionUID = 1L;
	public static final Long ID = 100000L;

	public EgtSpeciesCodeType() throws ProcessingException {
		super();
	}

	@Override
	public Long getId() {
		return ID;
	}

	public interface IEgtSpeciesCode {
		EgtSpecies getEnum();

		Long getId();

		String getText();
	}

	public Long getCodeIdByEnum(EgtSpecies e) {
		for (ICode<Long> c : getCodes()) {
			if (CompareUtility.equals(((IEgtSpeciesCode) c).getEnum(), e)) {
				return c.getId();
			}
		}
		return null;
	}

	public IEgtSpeciesCode getCodeByEnum(EgtSpecies e) {
		for (ICode<Long> c : getCodes()) {
			if (CompareUtility.equals(((IEgtSpeciesCode) c).getEnum(), e)) {
				return (IEgtSpeciesCode) c;
			}
		}
		return null;
	}

	@Order(10.0)
	public static class BlackCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100001L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("Black");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.BLACK;
		}

	}

	@Order(10.0)
	public static class WhiteCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100002L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("White");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.WHITE;
		}

	}

	@Order(10.0)
	public static class RedCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100003L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("Red");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.RED;
		}

	}

	@Order(10.0)
	public static class BlueCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100004L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("Blue");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.BLUE;
		}

	}

	@Order(10.0)
	public static class GreenCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100005L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("Green");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.GREEN;
		}

	}

	@Order(10.0)
	public static class YellowCode extends AbstractCode<Long> implements IEgtSpeciesCode {

		private static final long serialVersionUID = 1L;
		public static final Long ID = 100006L;

		@Override
		public Long getId() {
			return ID;
		}

		@Override
		public String getText() {
			return TEXTS.get("Yellow");
		}

		@Override
		public EgtSpecies getEnum() {
			return EgtSpecies.YELLOW;
		}

	}
}
