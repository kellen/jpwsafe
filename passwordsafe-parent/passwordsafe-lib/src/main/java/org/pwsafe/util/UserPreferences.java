package org.pwsafe.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pwsafe.lib.Util;

/**
 * Interface to all user preference activity (such as password policy, MRU, and
 * all that jazz).
 * It is mostly a copy from org.pwsafe.passwordsafeswt.UserPreferences, but we had to remove SWT stuff.
 *
 */
public class UserPreferences {

	// was über simple properties hinaus benötigt wird:
	// - needsSaving, getBoolean(String key)
	// TODO move because not dependent on JavaFx

	private static final Log log = LogFactory.getLog(UserPreferences.class);

	private static UserPreferences prefs;

	private static final String MRU = "mru.";
	private static int MAX_MRU = 5;

	public static final String PROPS_DIR = ".passwordsafe";
	private static final String PREFS_FILENAME = "preferences.properties";

	private final Properties prefStore;
	private boolean prefStoreNeedsSaving;
	/**
	 * Private constructor enforces singleton.
	 *
	 * @throws IOException
	 */
	private UserPreferences() throws IOException {
		prefStore = new Properties();
		loadPreferences();
	}

	/**
	 * Returns the name of the preference file.
	 *
	 * @return the name of the preferences file.
	 */
	public String getPreferencesFilename() {
		final String userDir = System.getProperty("user.home") + File.separator + PROPS_DIR
				+ File.separator + PREFS_FILENAME;
		return userDir;
	}

	/**
	 * Loads preferences from a properties file.
	 *
	 * @throws IOException if there are problems loading the preferences file
	 */
	private void loadPreferences() throws IOException {
		final String userFile = getPreferencesFilename();
		if (log.isDebugEnabled())
			log.debug("Loading from [" + userFile + "]");
		final File prefsFile = new File(userFile);
		if (!prefsFile.exists()) {
			final File prefsDir = new File(System.getProperty("user.home") + File.separator + PROPS_DIR);
			if (!prefsDir.exists()) {
				prefsDir.mkdir();
			}
		}

		if (prefsFile.exists()) {
			final FileReader fileReader = new FileReader(prefsFile);
			prefStore.load(fileReader);
		}
		// TODO: Check what happens if no file exists?

		if (log.isDebugEnabled())
			log.debug("Loaded " + prefStore + " preference settings from file");
	}

	/**
	 * Saves the preference to a properties file.
	 *
	 * @throws IOException if there are problems saving the file
	 */
	public void savePreferences() throws IOException {
		if (prefStoreNeedsSaving) {
			final String userFile = getPreferencesFilename();
			final FileWriter fileWriter = new FileWriter(userFile);
			if (log.isDebugEnabled())
				log.debug("Saving to [" + userFile + "]");
			prefStore.store(fileWriter, "Java Password Safe User Preferences");
			if (log.isDebugEnabled())
				log.debug("Saved " + prefStore + " preference settings to file");
		}
	}

	/**
	 * Sets the name of the most recently opened file.
	 *
	 * @param fileName the name of the file
	 */
	public void setMostRecentFilename(final String fileName) {
		prefStoreNeedsSaving = true; // TODO ob dies so richtig ist, ggf. ist fileName MRU i = altem Wert
		if (log.isDebugEnabled())
			log.debug("Setting most recently opened file to: [" + fileName + "]");

		try {
			loadPreferences(); // make sure we get the latest
		} catch (final IOException ioe) {
			log.error("Couldn't load preferences", ioe);
		}
		final Set<String> newMRU = new LinkedHashSet<String>(13);
		newMRU.add(fileName);
		newMRU.addAll(getMRUFiles());
		writeOutMruList(newMRU);
	}

	private void writeOutMruList(final Collection<String> newMRU) {
		int mruCounter = 0;
		for (final Iterator<String> iter = newMRU.iterator(); iter.hasNext() && mruCounter <= MAX_MRU;) {
			final String nextFilename = iter.next();
			final String mruKey = MRU + ++mruCounter;
			log.debug("Write MRU List key: " + mruKey + ", file: " + nextFilename);
			prefStore.put(mruKey, nextFilename);
		}
		// remove old values
		for (int i = MAX_MRU; i < MAX_MRU * 2; i++) {
			if (prefStore.contains(MRU + i)) {
				prefStore.put(MRU + i, "");
			}
		}
		try {
			savePreferences();
		} catch (final IOException e) {
			log.warn("Unable to save preferences file", e);
		}
	}

	/**
	 * Returns an array of recently opened filename (most recent to oldest).
	 *
	 * @return an array of recently opened filename, not null
	 */
	public List<String> getMRUFiles() {

		final List<String> allFiles = new LinkedList<String>();
		for (int i = 0; i <= MAX_MRU; i++) {
			final String nextFile = prefStore.getProperty(MRU + i);
			if (nextFile != null && nextFile.length() > 0)
				allFiles.add(nextFile);
		}
		return allFiles;

	}

	/**
	 * Convenience routine for getting most recently opened file.
	 *
	 * @return the filename of the MRU file (or null if there is no MRU file)
	 */
	public String getMRUFile() {

		final List<String> allMRU = getMRUFiles();
		if (allMRU.size() > 0) {
			return allMRU.get(0);
		} else {
			return null;
		}

	}

	public String getString(final String propName) {
		return prefStore.getProperty(propName);
	}

	public void setString(final String propName, final String propValue) {
		if(! Util.equals(propValue, prefStore.getProperty(propName))){
			prefStoreNeedsSaving = true;
			prefStore.setProperty(propName, propValue);
		}
	}

	public boolean getBoolean(final String propName) {
		return Boolean.parseBoolean(prefStore.getProperty(propName));
	}

	/**
	 * Singleton creator.
	 *
	 * @return a handle to the UserPreferences object for this user
	 */
	public static synchronized UserPreferences getInstance() {
		if (prefs == null) {
			try {
				prefs = new UserPreferences();
			} catch (final IOException e) {
				log.error("Couldn't load preferences file.", e);
			}
		}
		return prefs;
	}

	public static synchronized void reload() {
		// TODO nur mit getInstance zusammen ist es ein reload
		prefs = null;
	}

	/**
	 * Removes a file from the most recently used list.
	 *
	 * @param fileName
	 */
	public void removeMRUFile(final String fileName) {
		final List<String> files = getMRUFiles();
		if (files.contains(fileName)) {
			final int index = files.indexOf(fileName);
			files.remove(index);
			// set last MRU entry to empty:
			if (prefStore.contains(MRU + (MAX_MRU - 1))) {
				setString(MRU + (MAX_MRU - 1), "");
			}
			writeOutMruList(files);
		}
	}

}