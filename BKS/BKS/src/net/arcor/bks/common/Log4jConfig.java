/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/Log4jConfig.java-arc   1.2   May 30 2008 12:14:36   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/Log4jConfig.java-arc  $
 * 
 *    Rev 1.2   May 30 2008 12:14:36   makuier
 * Use xml format configuration.
 * 
 *    Rev 1.1   Nov 06 2007 17:55:28   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Aug 10 2007 18:01:24   makuier
 * Initial revision.
 * 
 *    Rev 1.0   Aug 06 2007 17:59:26   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

import java.net.URL;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class contains convenience methods to initialize and configure the
 * log4j logger.
 * @author goethalo
 */
public class Log4jConfig {

    /*------------------*
     * MEMBER VARIABLES *
     *------------------*/

    /**
     * The log4j logger.
     */
    private static Logger logger = Logger.getLogger(Log4jConfig.class);

    /**
     * Indicates whether this class has been initialized.
     */
    private static boolean initialized = false;

    /**
     * First line inserted in the log file.
     */
    private final static String newLoggingSession =
        "-----------------------------------------------------------------"
            + "---------------";

    /*---------*
     * METHODS *
     *---------*/

    /**
     * Initializes the logger based on the properties in a resource bundle.
     * This method reads the resource bundle, retrieves all the log4j-related
     * properties, and configures the logger based on these properties.
     * A property that is starting with 'log4j' is considered to be a log4j-
     * related property.
     * This method is useful because it allows to put the log4j properties in
     * the same property file as the application.
     *
     * @param configFile  the xml file to retrieve the log4j-related
     *               properties from.
     * @throws FIFException if the logger could not be initialized.
     */
    public static synchronized void init(URL configFile)
        throws Exception {
        // Check preconditions
        if (initialized) {
            throw new Exception("The logger has already been initialized");
        }
        if (configFile == null) {
            throw new Exception(
                "Cannot initialize logger because null "
                    + "file was passed in");
        }

         // Pass these properties to log4j
        DOMConfigurator.configure(configFile);

        // Remember that we successfully initialized
        initialized = true;
        logger.info(newLoggingSession);
        logger.info("Successfully initialized logger.");
    }

}
