package net.arcor.bks.common;

import net.arcor.bks.db.BksDataAccessObject;
import net.arcor.mcf2.exception.base.MCFException;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BksStaticContainer {

	private static FileSystemXmlApplicationContext ac = null;



	public static FileSystemXmlApplicationContext getAc() {
		return ac;
	}

	public static void setAc(FileSystemXmlApplicationContext appCon) {
		ac = appCon;
	}
	
	public static BksDataAccessObject getBksDataAccessObject(String preferedDataSource,String daoName) throws MCFException {
		BksDataAccessObject ret = null;
		ret = (BksDataAccessObject) ac.getBean(daoName);

		ret.connect(preferedDataSource);
		return ret;
	}

}
