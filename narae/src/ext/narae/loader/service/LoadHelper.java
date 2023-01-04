package ext.narae.loader.service;

import wt.services.ServiceFactory;

public class LoadHelper {

	public static final LoadService service = ServiceFactory.getService(LoadService.class);

	public static final LoadHelper manager = new LoadHelper();

}
