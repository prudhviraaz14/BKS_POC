/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/ibatis/BksDataAccessObjectImpl.java-arc   1.7   May 11 2015 13:26:26   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/ibatis/BksDataAccessObjectImpl.java-arc  $
 * 
 *    Rev 1.7   May 11 2015 13:26:26   makuier
 * refactor for HTTP client.
 * 
 *    Rev 1.6   Feb 17 2012 14:51:54   makuier
 * print the exception if connection to db fails.
 * 
 *    Rev 1.5   Oct 17 2011 16:45:28   makuier
 * getDaoFortargetDB added.
 * 
 *    Rev 1.4   Jan 24 2011 17:30:22   makuier
 * Changed the name of the beans.
 * 
 *    Rev 1.3   Dec 17 2010 17:21:48   makuier
 * getDataSourceName added.
 * 
 *    Rev 1.2   May 27 2009 16:00:42   makuier
 * MCF2 adaption
 * 
 *    Rev 1.1   May 30 2008 11:54:10   makuier
 * raise a warning instead of a stack trace is the connection to a datasource fails. 
 * 
 *    Rev 1.0   May 13 2008 13:11:08   makuier
 * Initial revision.
 * 
 */
package net.arcor.bks.db.ibatis;

import java.util.List;

import javax.sql.DataSource;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksStaticContainer;
import net.arcor.bks.db.BksDataAccessObject;
import net.arcor.mcf2.exception.base.MCFException;

import org.springframework.context.ApplicationContext;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;


public class BksDataAccessObjectImpl  extends SqlMapClientDaoSupport implements BksDataAccessObject {

	protected String dataSourceName = null;
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void connect(String preferedDataSource) throws MCFException  {
		ApplicationContext ac = BksStaticContainer.getAc();
		DataSource dataSource = null;
		if (preferedDataSource != null) {
			dataSource = (DataSource)ac.getBean("dataSource_"+preferedDataSource);
			dataSourceName = preferedDataSource;
			setDataSource(dataSource);
			if (isSchemaAvailable(false)){
				updateSession();
				return;
			}
		}
		dataSource = (DataSource)ac.getBean("dataSource_effonl");
		dataSourceName = "effonl";
		setDataSource(dataSource);
		if (isSchemaAvailable(false)){
			updateSession();
			return;
		}
		dataSource = (DataSource)ac.getBean("dataSource_onl");
		dataSourceName = "onl";
		setDataSource(dataSource);
		if (!isSchemaAvailable(false)){
			logger.error("No database connection available.");
			throw new MCFException("No database connection available.");
		}
		updateSession();
		return;
	}

	public void connectToTargetDb(String preferedDataSource)
	throws MCFException {
		ApplicationContext ac = BksStaticContainer.getAc();
		DataSource dataSource = null;
		if (preferedDataSource != null) {
			dataSource = (DataSource)ac.getBean("dataSource_"+preferedDataSource);
			dataSourceName = preferedDataSource;
			setDataSource(dataSource);
			if (isSchemaAvailable(true)){
				updateSession();
				return;
			}
		}
	}
	private void updateSession() {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        template.update("databaseclient.OptimizerGoal");
	}

	@SuppressWarnings("unchecked")
	private boolean isSchemaAvailable(Boolean throwException) throws MCFException{
		try {
			String schemaName = DatabaseClientConfig.getSetting("db."+dataSourceName+".SchemaName");
	        SqlMapClientTemplate template = getSqlMapClientTemplate();
	        List schema = template.queryForList("databaseclient.SchemaAvailablity", schemaName);
            return schema.isEmpty();
		} catch (Exception e) {
			if (throwException){
				logger.error("connection to database failed", e);
				throw new MCFException(e.getMessage());
			}
			logger.warn(e.getMessage());
			return false;
		}
	}

	public void closeConnection() throws Exception{
	}

}
