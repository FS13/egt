package org.eclipse.scout.apps.egt.client.ui.fields;

import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.annotations.ConfigProperty;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

public abstract class AbstractDoubleColumn extends AbstractDecimalColumn<Double> implements IDoubleColumn {

	public AbstractDoubleColumn() {
		super();
	}

	@Override
	protected int getConfiguredHorizontalAlignment() {
		return 1;
	}

	/*
	 * Configuration
	 */
	@Override
	@ConfigProperty(ConfigProperty.DOUBLE)
	@Order(200)
	protected Double getConfiguredMaxValue() {
		return null;
	}

	@Override
	@ConfigProperty(ConfigProperty.DOUBLE)
	@Order(210)
	protected Double getConfiguredMinValue() {
		return null;
	}

	/*
	 * Runtime
	 */

	@Override
	protected Double parseValueInternal(ITableRow row, Object rawValue) throws ProcessingException {
		Double validValue = null;
		if (rawValue == null) {
			validValue = null;
		} else if (rawValue instanceof Double) {
			validValue = (Double) rawValue;
		} else if (rawValue instanceof Number) {
			validValue = ((Number) rawValue).doubleValue();
		} else {
			throw new ProcessingException("invalid Double value in column '" + getClass().getSimpleName() + "': " + rawValue
					+ " class=" + rawValue.getClass());
		}
		return validValue;
	}

	@Override
	protected IDoubleField createDefaultEditor() {
		return new AbstractDoubleField() {
		};
	}

}
