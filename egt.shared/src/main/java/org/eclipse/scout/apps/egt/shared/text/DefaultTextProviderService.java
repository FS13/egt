package org.eclipse.scout.apps.egt.shared.text;

import org.eclipse.scout.apps.egt.shared.EgtSharedConfigProperties.EgtApplicationLanguageProperty;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.common.text.AbstractDynamicNlsTextProviderService;

/**
 * <h3>{@link DefaultTextProviderService}</h3>
 *
 * @author Fritz Schinkel
 */
@Order(-2000)
public class DefaultTextProviderService extends AbstractDynamicNlsTextProviderService {
	@Override
	protected String getDynamicNlsBaseName() {
		String language = CONFIG.getPropertyValue(EgtApplicationLanguageProperty.class);
		String suffix = StringUtility.isNullOrEmpty(language) ? "" : "_" + language;
		return "org.eclipse.scout.apps.egt.shared.texts.Texts" + suffix;
	}
}
