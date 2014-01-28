package net.worktrail.hub.sync.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertySyncStorage implements SyncStorage {
	
	private Properties props;
	private boolean dirty;
	private File file;
	
	public PropertySyncStorage(File file) {
		this.file = file;
		props = new Properties();
		if (file.exists()) {
			try {
				FileInputStream is = new FileInputStream(file);
				props.load(is);
			} catch (IOException e) {
				throw new RuntimeException("Error while loading properties.", e);
			}
		}
		dirty = false;
	}
	
	public void save() {
		if (dirty) {
			try {
				FileOutputStream out = new FileOutputStream(file);
				props.store(out, "");
				dirty = false;
			} catch (IOException e) {
				throw new RuntimeException("Error while storing output stream.", e);
			}
		}
	}

	@Override
	public void setString(String key, String value) {
		props.setProperty(key, value);
		dirty = true;
		save();
	}

	@Override
	public String getString(String key) {
		return props.getProperty(key);
	}

}
