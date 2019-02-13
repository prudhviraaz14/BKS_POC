package net.arcor.bks.common;

import org.aopalliance.intercept.MethodInvocation;

import de.arcor.aaw.ptf.interceptors.AbstractAawMonitor;

public class EaiInterceptor extends AbstractAawMonitor {
	
	@Override
	protected String createInvocationTraceName(MethodInvocation invocation) {
		StringBuilder queryBuf= new StringBuilder();
		if (invocation.getArguments().length >= 2) {
			String action = (String) invocation.getArguments()[1];
			queryBuf.append("Receive ServiceCall[");
			queryBuf.append(action);
			queryBuf.append("]");
		}
		return queryBuf.toString();
	}

	@Override
	protected String createLocalInformation(MethodInvocation invocation) {
		return null;
	}
}
