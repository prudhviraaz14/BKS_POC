package net.arcor.bks.db;

import net.arcor.bks.common.Cryptography;

import org.apache.commons.dbcp.BasicDataSource;

public class BksDataSource extends BasicDataSource {
	String encryptionKey = null;

	public void setPassword(String password) {
		if (encryptionKey != null) {
			try {
				String decryptPasswd = Cryptography.ccbDecrypt(encryptionKey, password,true);
				super.setPassword(decryptPasswd);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		else
			super.setPassword(password);
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

}
