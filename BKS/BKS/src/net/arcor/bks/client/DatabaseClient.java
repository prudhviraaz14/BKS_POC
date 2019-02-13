/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/DatabaseClient.java-arc   1.11   Jun 03 2015 08:44:26   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/DatabaseClient.java-arc  $
 * 
 *    Rev 1.11   Jun 03 2015 08:44:26   makuier
 * Refactor to adapt to EAI.
 * 
 *    Rev 1.10   Oct 17 2011 16:46:24   makuier
 * getDaoFortargetDB added.
 * 
 *    Rev 1.9   May 27 2009 15:57:36   makuier
 * MCF2 adaption.
 * 
 *    Rev 1.8   Mar 12 2009 14:49:56   makuier
 * Changed the path to the cache configuration file.
 * 
 *    Rev 1.7   May 30 2008 13:41:16   makuier
 * use xml forma for log4j
 * 
 *    Rev 1.6   May 13 2008 11:41:14   makuier
 * removed the shutdown hook.
 * 
 *    Rev 1.5   Feb 27 2008 10:14:48   makuier
 * Instantiate DAO through bean.
 * 
 *    Rev 1.4   Feb 12 2008 11:06:12   makuier
 * Terminate the application context when tomcat shuts down.
 * 
 *    Rev 1.3   Dec 06 2007 15:05:52   makuier
 * Get the dao bean from application context.
 * 
 *    Rev 1.2   Nov 27 2007 13:57:44   makuier
 * Make application context visible.
 * 
 *    Rev 1.1   Nov 06 2007 17:56:22   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Aug 10 2007 18:00:16   makuier
 * Initial revision.
*/
package net.arcor.bks.client;

import java.net.URL;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;

import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.BksStaticContainer;
import net.arcor.bks.common.Log4jConfig;
import net.arcor.bks.db.BksDataAccessObject;
import net.arcor.mcf2.exception.base.MCFException;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public final class DatabaseClient extends HttpServlet {

	private static final ResourceBundle rb = ResourceBundle.getBundle("config/BksClient");
    /**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	/*------------------*
     * MEMBER VARIABLES *
     *------------------*/

	private static FileSystemXmlApplicationContext ac = null;
    /**
     * The log4j logger.
     */
    private static Logger logger = Logger.getLogger(DatabaseClient.class);

    /**
     * Indicates whether there is an error status.
     */
    private static boolean errorStatus = false;

    /**
     * Indicates whether the shutdown hook is being invoked.
     */
    private static boolean shutDownHookInvoked = false;

    /**
     * Indicates whether this class is initialzed.
     */
    private static boolean initialized = false;

    /**
     * Object used for locking.
     */
    private static final Object lock = new Object();

	/**
	 * The literal for the status Canceled in the database.
	 */
	public static final String requestStatusDataTypeVarchar = "VARCHAR";
	
	/**
	 * The literal for the status Canceled in the database.
	 */
	public static final String requestStatusDataTypeNumber = "NUMBER";
	
    /**
     * Indicates whether the client should shut down when all requests
     * have been processed. I.e. when there are no more NOT_STARTED entries
     * in the database.
     */
    public static boolean autoShutdown = false;

    /*---------*
     * METHODS *
     *---------*/

    /**
     * Indicates whether the client is in error status.
     * @return the error status
     */
    public static boolean inErrorStatus() {
        synchronized (lock) {
            return errorStatus;
        }
    }

    /**
     * Sets the client in error status.
     */
    protected static void setErrorStatus() {
        synchronized (lock) {
            errorStatus = true;
        }
    }

    /**
     * Indicates whether the shutdown hook is invoked.
     * @return true if invoked, false if not.
     */
    protected static boolean isShutDownHookInvoked() {
        synchronized (lock) {
            return shutDownHookInvoked;
        }
    }

    /**
     * Sets the shutdown hook invocation to true.
     */
    protected static void setShutDownHookInvoked() {
        synchronized (lock) {
            shutDownHookInvoked = true;
        }
    }

    /**
     * Determines whether this class is initialized.
     * @return true if initialize, false if not.
     */
    protected static boolean isInitialized() {
        return initialized;
    }

    /**
     * Initializes the application.
     * @param configFile  the name of the property file to get the settings from.
     * @throws FIFException if the application could not be initialized.
     */
    public static synchronized void initProperties() throws Exception {
        // Bail out if the client was initialized already
        if (initialized == true) {
            return;
        }

        // Set the initialized flag
        initialized = true;

        // Initialize the queue client config
        DatabaseClientConfig.init(rb);

        // Initialize the logger
        URL url = Loader.getResource("log4j.xml");
        Log4jConfig.init(url);

        logger.info(
            "Initializing DatabaseClient with property file "
                + "...");

        logger.info("Successfully initialized DatabaseClient.");
    }

    /**
     * Starts the client.
     * @throws Exception 
     * @throws FIFException if the client could not be started.
     */
    public static void start() throws Exception {
    	String beans;
		try {
			beans = DatabaseClientConfig.getSetting("databaseclient.BeanConfigurationFile");
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
    	final String [] beanConfiguration = beans.split(",");
		ac = new FileSystemXmlApplicationContext(beanConfiguration);
		BksStaticContainer.setAc(ac);
		ac.registerShutdownHook();
		BksRefDataCacheHandler.loadAllRefData();
     }

    /**
     * Starts the <code>DatabaseClient</code>.
     */
    public void init () {
    	// Initialize the application
    	try {
    		initProperties();
    		// Start the database client
    		start();
    	} catch (final Exception e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    }

    public void destroy(){
    	ac.close();
   }

	public static BksDataAccessObject getBksDataAccessObject(String preferedDataSource,String daoName) throws MCFException {
		return BksStaticContainer.getBksDataAccessObject(preferedDataSource, daoName);
	}

	public static BksDataAccessObject getBksDaoForTargetDb(String preferedDataSource,String daoName) throws MCFException {
		BksDataAccessObject ret = null;
		ret = (BksDataAccessObject) ac.getBean(daoName);

		ret.connectToTargetDb(preferedDataSource);
		return ret;
	}

	public static FileSystemXmlApplicationContext getAc() {
		return ac;
	}

	public static void setAc(FileSystemXmlApplicationContext ac) {
		DatabaseClient.ac = ac;
	}
}
