package net.worktrail.hub.sync.git;

public interface SyncStorage {
	public void setString(String key, String value);
	public String getString(String key);
}
