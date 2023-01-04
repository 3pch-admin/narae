package com.ptc.wvs.util;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;

import com.ptc.wvs.common.util.WVSProperties;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.log4j.LogR;

/**
 * Use Case: Administrator wants to control what objects get published by
 * filtering publish requests based on the file type, lifecycle and container.
 * 
 * In this example, EPMDocuments are filtered by their file extension, lifecycle
 * & container and only files with extensions, lifecycles and containers that
 * match a list specified in the properties file will be published/NOT
 * published.
 * 
 * Add following lines to $wt_home/codebase/WEB-INF/conf/wvs.properties to
 * execute this hook:
 * publish.service.filterepmdocumentpublishmethod=com.ptc.wvs.util.EPMDocumentPublishFilters/filterEPMDocumentPublish
 * publish.service.filterpublishmethod=com.ptc.wvs.util.EPMDocumentPublishFilters/filterPublish
 *
 * ------------------------------------------------------------------------------------------------------------------------
 * publish.service.filterepmdocumentpublishmethod.defaultrule=exclude
 * publish.service.filterepmdocumentpublishmethod.includelist=CONTAINERS>*;EXTENSIONS>drw,idw,SLDDRW,CATDrawing;LIFECYCLES>RELEASED,INWORK
 * Note. publish.service.filterepmdocumentpublishmethod.includelist property
 * should contain a list of file extensions, lifecycle status and containers
 * that should be published. The list here is just provided as an example. Note.
 * (*) means wildcard character
 * ------------------------------------------------------------------------------------------------------------------------
 * OR
 * ------------------------------------------------------------------------------------------------------------------------
 * publish.service.filterepmdocumentpublishmethod.defaultrule=include
 * publish.service.filterepmdocumentpublishmethod.excludelist=CONTAINERS>*;EXTENSIONS>frm,lay;LIFECYCLES>*
 * publish.service.filterepmdocumentpublishmethod.excludestartsNumberlist=NS;
 * Now Not Use.
 * publish.service.filterepmdocumentpublishmethod.includestartsNumberlist=NS;NA;NB;NP;NZ;
 * Note. publish.service.filterepmdocumentpublishmethod.excludelist property
 * should contain a list of file extensions, lifecycle status and containers
 * that should NOT be published. The list here is just provided as an example.
 * ------------------------------------------------------------------------------------------------------------------------
 *
 * See the documentation in $wt_home/codebase/WEB-INF/conf/wvs.properties.xconf
 * for more information on these properties.
 */

public class EPMDocumentPublishFilters {
	private static final Logger logger = LogR.getLoggerInternal(EPMDocumentPublishFilters.class.getName());
	private static Set<String> types = new HashSet<String>();
	private static Set<String> lifecycles = new HashSet<String>();
	private static Set<String> containers = new HashSet<String>();

	private static Boolean defaultExcludeRule = true;
	private static Set<String> conditions = new HashSet<String>();
	private static Set<String> conditions_excludestartsNumberlist = new HashSet<String>();
	private static Set<String> conditions_includestartsNumberlist = new HashSet<String>();

	static {
		// get the property value for the list of types
		String defaultRule = WVSProperties
				.getPropertyValue("publish.service.filterepmdocumentpublishmethod.defaultrule");
		String includeList = WVSProperties
				.getPropertyValue("publish.service.filterepmdocumentpublishmethod.includelist");
		String excludeList = WVSProperties
				.getPropertyValue("publish.service.filterepmdocumentpublishmethod.excludelist");
		String excludestartsNumberlist = WVSProperties
				.getPropertyValue("publish.service.filterepmdocumentpublishmethod.excludestartsNumberlist");
		String includestartsNumberlist = WVSProperties
				.getPropertyValue("publish.service.filterepmdocumentpublishmethod.includestartsNumberlist");

		if (defaultRule.equalsIgnoreCase("include")) {
			defaultExcludeRule = false;
		}

		if (defaultExcludeRule) {

			if (includeList == null) {
				logger.error(
						"The property publish.service.filterepmdocumentpublishmethod.includelist must be configured to use this filter.");
			} else if (includeList.length() > 0) {
				StringTokenizer tok = new StringTokenizer(includeList.trim(), ";");
				while (tok.hasMoreTokens()) {
					conditions.add(tok.nextToken());
				}
				if (logger.isDebugEnabled()) {
					logger.debug(
							"publish.service.filterepmdocumentpublishmethod.includelist: " + conditions.toString());
				}
			} else {
				logger.debug("publish.service.filterepmdocumentpublishmethod.includelist is blank");
			}

		} else {

			if (excludeList == null) {
				logger.error(
						"The property publish.service.filterepmdocumentpublishmethod.excludelist must be configured to use this filter.");
			} else if (excludeList.length() > 0) {
				StringTokenizer tok2 = new StringTokenizer(excludeList.trim(), ";");
				while (tok2.hasMoreTokens()) {
					conditions.add(tok2.nextToken());
				}
				if (logger.isDebugEnabled()) {
					logger.debug(
							"publish.service.filterepmdocumentpublishmethod.excludelist: " + conditions.toString());
				}
			} else {
				logger.debug("publish.service.filterepmdocumentpublishmethod.excludelist is blank");
			}
			if (excludestartsNumberlist == null) {
				logger.error(
						"The property publish.service.filterepmdocumentpublishmethod.excludestartsNumberlist must be configured to use this filter.");
			} else if (excludestartsNumberlist.length() > 0) {
				StringTokenizer tok2 = new StringTokenizer(excludestartsNumberlist.trim(), ";");
				while (tok2.hasMoreTokens()) {
					conditions_excludestartsNumberlist.add(tok2.nextToken());
				}
				if (logger.isDebugEnabled()) {
					logger.debug("publish.service.filterepmdocumentpublishmethod.excludestartsNumberlist: "
							+ conditions_excludestartsNumberlist.toString());
				}
			} else {
				logger.debug("publish.service.filterepmdocumentpublishmethod.excludestartsNumberlist is blank");
			}

			if (includestartsNumberlist == null) {
				logger.error(
						"The property publish.service.filterepmdocumentpublishmethod.includestartsNumberlist must be configured to use this filter.");
			} else if (includestartsNumberlist.length() > 0) {
				StringTokenizer tok2 = new StringTokenizer(includestartsNumberlist.trim(), ";");
				while (tok2.hasMoreTokens()) {
					conditions_includestartsNumberlist.add(tok2.nextToken());
				}
				if (logger.isDebugEnabled()) {
					logger.debug("publish.service.filterepmdocumentpublishmethod.includestartsNumberlist: "
							+ conditions_includestartsNumberlist.toString());
				}
			} else {
				logger.debug("publish.service.filterepmdocumentpublishmethod.includestartsNumberlist is blank");
			}
		}

	}

	/**
	 * Method to do custom filtering of the EPMDocuments that will be published as a
	 * result of an CheckIn Complete or Ready to Publish events. In this example,
	 * just call the {@link #filterPublish(Persistable, Boolean)} method since that
	 * contains the logic needed to filter
	 * 
	 * If required, configure with property:
	 * publish.service.filterepmdocumentpublishmethod
	 * 
	 * @param doc The EPMDocument to filter
	 * @return True if the EPMDocument should be published, false otherwise
	 */
	public static Boolean filterEPMDocumentPublish(EPMDocument doc) {
		logger.info("Doing the EPMDocumentPublishFilters filterEPMDocumentPublish");
		return filterPublish(doc, true);
	}

	/**
	 * Method called before all publishing to do custom filtering of Objects that
	 * can be published.
	 * 
	 * If required, configure with property: publish.service.filterpublishmethod
	 * 
	 * @param persistable The persistable object to filter
	 * @param fromDB      is this data stored in Windchill
	 * @return True if the object should be published, false otherwise. In this
	 *         example, if the Persistable object is not an EPMDocument, this will
	 *         return true. If it is an EPMDocument and
	 *         publish.service.filterepmdocumentpublishmethod.defaultrule=exclude,
	 *         then it will only return true if the file extension, lifecycle status
	 *         and container match all the 3 conditions in
	 *         publish.service.filterepmdocumentpublishmethod.includelist property
	 */
	public static Boolean filterPublish(Persistable persistable, Boolean fromDB) {
		logger.info("Doing the EPMDocumentPublishFilters filterPublish");

		if (!(persistable instanceof EPMDocument)) {
			logger.info("This object is not an EPMDocument, publish");
			return Boolean.TRUE;
		}

		String cadName = ((EPMDocument) persistable).getCADName();
		String lifecycleState = ((EPMDocument) persistable).getLifeCycleState().toString();
		String containerName = ((EPMDocument) persistable).getContainer().getName();

		Boolean publishableContainer = false;
		Boolean publishableType = false;
		Boolean publishableLifecycle = false;

		// check the name of the EPMDocument
		if (cadName == null || cadName.length() == 0) {
			logger.info("The CAD Name is empty, do not publish");
			return Boolean.FALSE;
		}

		// check to make sure there is a file extension to check
		boolean startWordCheck = getStartWordCheck(cadName);
		if (startWordCheck) {
			logger.info("The file startWord is do not publish +\tcadName=" + cadName);
			return Boolean.FALSE;
		}

		// check to make sure there is a file extension to check
		String type = getExtension(cadName);
		if (type == null || type.length() == 0) {
			logger.info("The file extension is empty, do not publish");
			return Boolean.FALSE;
		}

		// see if the type of this EPMDocument is in the list of valid types
		if (getTypes().contains("*") || getTypes().contains(type.toUpperCase())) {
			logger.info("The file extension (" + type.toUpperCase() + ") is in the list of valid types");
			publishableType = true;
		} else {
			logger.info("The file extension (" + type.toUpperCase() + ") is NOT in the list of valid types");
		}

		// see if the lifecycle of this EPMDocument is in the list of valid lifecycles
		if (getLifecycles().contains("*") || getLifecycles().contains(lifecycleState)) {
			logger.info("The lifecycle (" + lifecycleState + ") is in the list of valid lifecycles");
			publishableLifecycle = true;
		} else {
			logger.info("The lifecycle (" + lifecycleState + ") is NOT in the list of valid lifecycles");
		}

		// see if the container of this EPMDocument is in the list of valid containers
		if (getContainers().contains("*") || getContainers().contains(containerName)) {
			logger.info("The container (" + containerName + ") is in the list of valid containers");
			publishableContainer = true;
		} else {
			logger.info("The container (" + containerName + ") is NOT in the list of valid containers");
		}

		if (defaultExcludeRule) {
			if (publishableType & publishableLifecycle & publishableContainer) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}

		if (publishableType & publishableLifecycle & publishableContainer) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;

	}

	private static boolean getStartWordCheck(String cadName) {
		boolean isCheck = false;
		if (conditions_excludestartsNumberlist.size() > 0) {
			isCheck = false;
			String[] array = conditions_excludestartsNumberlist.toArray(new String[0]);
			logger.info("***** array.length: " + array.length);
			for (int i = 0; i < array.length; i++) {
				if (null != array[i] && array[i].length() > 0) {
					logger.info("***** cadName: " + cadName.toUpperCase() + "\tarray[" + i + "]=" + array[i]);
					if (cadName.toUpperCase().startsWith(array[i])) {
						isCheck = true;
						break;
					}
				}
			}
		}
		if (conditions_includestartsNumberlist.size() > 0) {
			isCheck = true;
			String[] array = conditions_includestartsNumberlist.toArray(new String[0]);
			logger.info("***** array.length: " + array.length);
			for (int i = 0; i < array.length; i++) {
				if (null != array[i] && array[i].length() > 0) {

					if (cadName.toUpperCase().startsWith(array[i])) {
						isCheck = false;
						logger.info("***** cadName: " + cadName.toUpperCase() + "\tarray[" + i + "]=" + array[i]
								+ "isCheck=" + isCheck);
						break;
					}
				}
			}
		}
		return isCheck;
	}

	/**
	 * Get the types that were found in the property
	 * 
	 * @return
	 */
	public static Set<String> getTypes() {
		String[] array = conditions.toArray(new String[0]);
		for (int i = 0; i < 3; i++) {
			if (array[i].indexOf("EXTENSIONS>") != -1) {
				array[i] = array[i].replace("EXTENSIONS>", "");
				StringTokenizer tok = new StringTokenizer(array[i].trim(), ",");
				while (tok.hasMoreTokens()) {
					types.add(tok.nextToken().toUpperCase());
				}
//				System.out.println("***** types: " + types);
			}
		}
		return types;
	}

	/**
	 * Get the lifecycles that were found in the property
	 * 
	 * @return
	 */
	public static Set<String> getLifecycles() {
		String[] array = conditions.toArray(new String[0]);
		for (int i = 0; i < 3; i++) {
			if (array[i].indexOf("LIFECYCLES>") != -1) {
				array[i] = array[i].replace("LIFECYCLES>", "");
				StringTokenizer tok = new StringTokenizer(array[i].trim(), ",");
				while (tok.hasMoreTokens()) {
					lifecycles.add(tok.nextToken().toUpperCase());
				}
//				System.out.println("***** lifecycles: " + lifecycles);
			}
		}
		return lifecycles;
	}

	/**
	 * Get the containers that were found in the property
	 * 
	 * @return
	 */
	public static Set<String> getContainers() {
		String[] array = conditions.toArray(new String[0]);
		for (int i = 0; i < 3; i++) {
			if (array[i].indexOf("CONTAINERS>") != -1) {
				array[i] = array[i].replace("CONTAINERS>", "");
				StringTokenizer tok = new StringTokenizer(array[i].trim(), ",");
				while (tok.hasMoreTokens()) {
					containers.add(tok.nextToken());
				}
//				System.out.println("***** containers: " + containers);
			}
		}
		return containers;
	}

	/**
	 * Utility method to find the extension of a file
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getExtension(String fileName) {
		String result = null;
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot >= 0) {
			result = fileName.substring(lastDot + 1);
		}
		return result;

	}

}