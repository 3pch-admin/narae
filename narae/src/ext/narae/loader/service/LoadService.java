package ext.narae.loader.service;

import java.util.Hashtable;

import wt.method.RemoteInterface;

@RemoteInterface
public interface LoadService {

	public void loadUserFromExcel(Hashtable hash) throws Exception;
}
