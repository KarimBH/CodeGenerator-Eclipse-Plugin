package com.kbh.classesfromxsd.preferencePages;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kbh.classesfromxsd.wizards.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(GeneralPreferencesPage.XJC_CUSTOM_PACKAGE,
				false);
		
		store.setDefault(GeneralPreferencesPage.XJC_PACKAGE_NAME,
				"jaxb.generated");
	
		store.setDefault(GeneralPreferencesPage.JAXB_SOURCE_DIRECTORY,
				"/generated-sources/jaxb");
		store.setDefault(GeneralPreferencesPage.XJC_DESTINATION_DIRECTORY,
				"/generated-sources/jaxb");
		store.setDefault(GeneralPreferencesPage.COMMANDS_PACKAGE_NAME,
				"generated-sources/commands");
		store.setDefault(GeneralPreferencesPage.ENDPOINTS_PACKAGE_NAME,
				"generated-sources/endpoints");
		store.setDefault(GeneralPreferencesPage.SHOW_GENERATION_RESULT,
				true);
		store.setDefault(GeneralPreferencesPage.GENERATE_COMMANDS, true);
		
		store.setDefault(GeneralPreferencesPage.GENERATE_ENDPOINTS, true);

	}
}