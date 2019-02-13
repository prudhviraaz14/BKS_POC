package net.arcor.bks.common;

import org.aopalliance.intercept.MethodInvocation;

import de.arcor.aaw.ptf.interceptors.AbstractAawMonitor;

public class DBAccessInterceptor extends AbstractAawMonitor {
	private int maxStatementLength = 40;
	
	@Override
	protected String createInvocationTraceName(MethodInvocation invocation) {
		String query = invocation.getMethod().getName();
		// clip query
		int max = maxStatementLength > query.length() ? query.length() : maxStatementLength;
		
		StringBuilder queryBuf = new StringBuilder();
		queryBuf.append(query.substring(0, max));
		queryBuf.append(" ...");
		return queryBuf.toString();
	}

	@Override
	protected String createLocalInformation(MethodInvocation invocation) {
		Object[] params = invocation.getArguments();

		StringBuilder queryBuf = null;
		if (params != null && params.length > 0) {
			queryBuf = new StringBuilder();
			queryBuf.append("Parameters: ");
			for (Object param : params) {
				if (param != null)
				    queryBuf.append(param.toString()).append(" ");
				else
					queryBuf.append("null ");
			}
		}
		
		return queryBuf == null ? null : queryBuf.toString();
	}

	public void setMaxStatementLength(int maxStatementLength) {
		this.maxStatementLength = maxStatementLength;
	}

}
