package org.eclipse.scout.apps.egt.client.ui.fields;

import java.math.BigDecimal;

import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.AbstractDecimalField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.annotations.ConfigProperty;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

public abstract class AbstractDoubleField extends AbstractDecimalField<Double> implements IDoubleField {

	public AbstractDoubleField() {
		this(true);
	}

	public AbstractDoubleField(boolean callInitializer) {
		super(callInitializer);
	}

	/*
	 * Configuration
	 */

	/**
	 * @deprecated Will be removed in the 5.0 Release., use
	 *             {@link #getConfiguredMinValue()}.<br>
	 *             As long as this deprecated version is overridden in subclasses.
	 *             This setting wins over {@link #getConfiguredMinValue()} in
	 *             {@link #initConfig()}.
	 */
	@Deprecated
	protected Double getConfiguredMinimumValue() {
		return getConfiguredMinValue();
	}

	/**
	 * @deprecated Will be removed in the 5.0 Release., use
	 *             {@link #getConfiguredMaxValue()}.<br>
	 *             As long as this deprecated version is overridden in subclasses.
	 *             This setting wins over {@link #getConfiguredMaxValue()} in
	 *             {@link #initConfig()}.
	 */
	@Deprecated
	protected Double getConfiguredMaximumValue() {
		return getConfiguredMaxValue();
	}

	@ConfigProperty(ConfigProperty.DOUBLE)
	@Order(300)
	@Override
	protected Double getConfiguredMinValue() {
		return -Double.MAX_VALUE;
	}

	@ConfigProperty(ConfigProperty.DOUBLE)
	@Order(310)
	@Override
	protected Double getConfiguredMaxValue() {
		return Double.MAX_VALUE;
	}

	@Override
	protected Double getMinPossibleValue() {
		return -Double.MAX_VALUE;
	}

	@Override
	protected Double getMaxPossibleValue() {
		return Double.MAX_VALUE;
	}

	@Override
	protected void initConfig() {
		super.initConfig();
		setMinValue(getConfiguredMinimumValue());
		setMaxValue(getConfiguredMaximumValue());
	}

	/**
	 * uses {@link #parseToBigDecimalInternal(String)} to parse text and returns
	 * the result as Double
	 */
	@Override
	protected Double parseValueInternal(String text) throws ProcessingException {
		Double retVal = null;
		BigDecimal parsedVal = parseToBigDecimalInternal(text);
		if (parsedVal != null) {
			retVal = Double.valueOf(parsedVal.doubleValue());
		}
		return retVal;
	}
}
