/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/DatabaseClientConfig.java-arc   1.1   Nov 06 2007 17:56:22   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/DatabaseClientConfig.java-arc  $
 * 
 *    Rev 1.1   Nov 06 2007 17:56:22   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Aug 10 2007 18:00:16   makuier
 * Initial revision.
*/
package net.arcor.bks.client;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class contains the configuration for the DatabaseConfig application.
 */
public class DatabaseClientConfig {

    /*------------------*
     * MEMBER VARIABLES *
     *------------------*/

    /**
     * The properties to get the configuration settings from.
     */
    private static Properties props = null;

    /*---------*
     * METHODS *
     *---------*/

    /**
     * Initializes the <code>DatabaseClient</code> application.
     * @param configFile  the name of the property file to get the settings from.
     * @throws FIFException
     */
    public static synchronized void init(ResourceBundle rb){
        // Read the configuration
        props = new Properties();
        Enumeration keys = rb.getKeys();
        while ((keys != null) && (keys.hasMoreElements())) {
            String key = (String) keys.nextElement();
            if (!key.toLowerCase().startsWith("log4j")) {
            	props.put(key, rb.getString(key));
            }
        }
    }

    /**
     * Gets a configuration setting from the configuration file.
     * @param key  the key to get the setting for
     * @return a <code>String</code> containing the setting
     * @throws FIFException if the key was not found in the
     * configuration file.
     */
    public static String getSetting(String key) throws Exception {
        String value = props.getProperty(key);
        if (value != null) {
            value = value.trim();
        } else {
            throw new Exception(
                "Missing key in configuration file. Key: " + key);
        }
        return value;
    }

    /**
     * Gets a configuration setting from the configuration file
     * formatted as a path.
     * The absolute path of the settings is constructed, including the
     * ending slash.
     * @param key  the key to get the setting for
     * @return a <code>String</code> containing the absolute path.
     * @throws FIFException if the key was not found in the
     * configuration file.
     */
    public static String getPath(String key) throws Exception {
        String path = new File(getSetting(key)).getAbsolutePath();
        if (!path.endsWith(System.getProperty("file.separator"))) {
            path += System.getProperty("file.separator");
        }
        return path;
    }

    /**
     * Gets a configuration setting from the configuration file.
     * @param key  the key to get the setting for.
     * @return true if the value of the key is "true", false if not.
     * @throws FIFException if the key was not found in the configuration file.
     */
    public static boolean getBoolean(String key) throws Exception {
        return (getSetting(key).toLowerCase().equals("true"));
    }

    /**
     * Gets a configuration setting from the configuration file.
     * @param key  the key to get the setting for.
     * @return the <code>int</code> value of the setting.
     * @throws FIFException if the key was not found in the configuration file.
     */
    public static int getInt(String key) throws Exception {
        int value = 0;
        try {
            value = Integer.parseInt(getSetting(key));
        } catch (NumberFormatException nfe) {
            throw new Exception(
                "Configuration value should be a number.  Key: " + key,
                nfe);
        }
        return (value);
    }
}
