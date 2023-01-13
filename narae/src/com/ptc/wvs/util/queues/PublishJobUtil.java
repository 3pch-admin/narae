// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PublishJobUtil.java

package com.ptc.wvs.util.queues;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.wvs.common.util.WVSProperties;

import ext.narae.service.drawing.beans.DrawingHelper2;
import ext.narae.service.part.beans.PartSearchHelper;
import ext.narae.util.StringUtil;
import ext.narae.util.iba.IBAUtil;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;

public class PublishJobUtil {

	public PublishJobUtil() {
	}

	public static String[] filterQueueSet(Persistable persistable, String s, Integer integer, Integer integer1,
			String s1, String s2, String s3, String s4) {
		String as[] = { s1, s2, s3, s4 };
		try {
			if (persistable instanceof EPMDocument) {
				EPMDocument epmdocument = (EPMDocument) persistable;
				EPMDocumentType epmdocumenttype = epmdocument.getDocType();
				WTPart wtpart = DrawingHelper2.getWTPart(epmdocument);
				if (null == wtpart) {
					String s5 = "";
					System.out.println(
							(new StringBuilder()).append("cad number : ").append(epmdocument.getNumber()).toString());
					if (epmdocument.getNumber().toUpperCase().indexOf(".") > 0) {
						String s6 = epmdocument.getNumber().toUpperCase().substring(0,
								epmdocument.getNumber().toUpperCase().lastIndexOf("."));
						System.out.println((new StringBuilder()).append("part number : ").append(s6).toString());
						wtpart = PartSearchHelper.getWTPart(s6);
					}
					if (epmdocument.getNumber().toUpperCase().indexOf("_2D") > 0) {
						String s7 = epmdocument.getNumber().toUpperCase().substring(0,
								epmdocument.getNumber().toUpperCase().lastIndexOf("_2D"));
						System.out.println((new StringBuilder()).append("part number : ").append(s7).toString());
						wtpart = PartSearchHelper.getWTPart(s7);
					}
				}
				String s8 = StringUtil.checkNull(IBAUtil.getAttrValue(epmdocument, "P_Name"));
				String s9 = StringUtil.checkNull(IBAUtil.getAttrValue(epmdocument, "Part_Name"));
				String s10 = StringUtil.checkNull(IBAUtil.getAttrValue(epmdocument, "PART_NAME"));
				if (s8.length() == 0 && epmdocument.getName().length() > 0) {
					IBAUtil.changeIBAValue(epmdocument, "P_Name", epmdocument.getName());
					if (null != wtpart) {
						EPMDocument epmdocument1 = DrawingHelper2.getEPMDocument(wtpart);
						EPMDocument epmdocument4;
						for (QueryResult queryresult = VersionControlHelper.service
								.allIterationsOf(epmdocument1.getMaster()); queryresult.hasMoreElements(); IBAUtil
										.changeIBAValue(epmdocument4, "PART_NAME", wtpart.getName())) {
							RevisionControlled revisioncontrolled = (RevisionControlled) queryresult.nextElement();
							epmdocument4 = (EPMDocument) revisioncontrolled;
							IBAUtil.changeIBAValue(epmdocument4, "P_Name", wtpart.getName());
							IBAUtil.changeIBAValue(epmdocument4, "Part_Name", wtpart.getName());
						}

						IBAUtil.changeIBAValue(epmdocument, "P_Name", wtpart.getName());
					}
				}
				if (s9.length() == 0 && epmdocument.getName().length() > 0) {
					IBAUtil.changeIBAValue(epmdocument, "Part_Name", epmdocument.getName());
					if (null != wtpart) {
						EPMDocument epmdocument2 = DrawingHelper2.getEPMDocument(wtpart);
						EPMDocument epmdocument5;
						for (QueryResult queryresult1 = VersionControlHelper.service
								.allIterationsOf(epmdocument2.getMaster()); queryresult1.hasMoreElements(); IBAUtil
										.changeIBAValue(epmdocument5, "PART_NAME", wtpart.getName())) {
							RevisionControlled revisioncontrolled1 = (RevisionControlled) queryresult1.nextElement();
							epmdocument5 = (EPMDocument) revisioncontrolled1;
							IBAUtil.changeIBAValue(epmdocument5, "P_Name", wtpart.getName());
							IBAUtil.changeIBAValue(epmdocument5, "Part_Name", wtpart.getName());
						}

						IBAUtil.changeIBAValue(epmdocument, "P_Name", wtpart.getName());
					}
				}
				if (s10.length() == 0 && epmdocument.getName().length() > 0) {
					IBAUtil.changeIBAValue(epmdocument, "PART_NAME", epmdocument.getName());
					if (null != wtpart) {
						EPMDocument epmdocument3 = DrawingHelper2.getEPMDocument(wtpart);
						EPMDocument epmdocument6;
						for (QueryResult queryresult2 = VersionControlHelper.service
								.allIterationsOf(epmdocument3.getMaster()); queryresult2.hasMoreElements(); IBAUtil
										.changeIBAValue(epmdocument6, "PART_NAME", wtpart.getName())) {
							RevisionControlled revisioncontrolled2 = (RevisionControlled) queryresult2.nextElement();
							epmdocument6 = (EPMDocument) revisioncontrolled2;
							IBAUtil.changeIBAValue(epmdocument6, "P_Name", wtpart.getName());
							IBAUtil.changeIBAValue(epmdocument6, "Part_Name", wtpart.getName());
						}

						IBAUtil.changeIBAValue(epmdocument, "P_Name", wtpart.getName());
					}
				}
				LWCNormalizedObject lwcnormalizedobject = new LWCNormalizedObject(persistable, null, Locale.US,
						new DisplayOperationIdentifier());
				lwcnormalizedobject.load(new String[] { IBA_IDENTIFIER });
				String s11 = (String) lwcnormalizedobject.getAsString(IBA_IDENTIFIER);
				if (VERBOSE) {
					System.out.println("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet() START");
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param cadDoc       : ")
							.append(persistable).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param AuthoringApp : ")
							.append(s).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param EPMDocType   : ")
							.append(epmdocumenttype).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param IBA          : ")
							.append(s11).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param rqstType     : ")
							.append(integer).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param rqstSource   : ")
							.append(integer1).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param rqstPriority : ")
							.append(s1).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param rqstSet      : ")
							.append(s2).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param repName      : ")
							.append(s3).toString());
					System.out.println((new StringBuilder())
							.append("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Param repDesc      : ")
							.append(s4).toString());
				}
				if (s != null && epmdocumenttype != null) {
					String s12 = (new StringBuilder()).append(s).append(epmdocumenttype.toString()).toString();
					if (s11 != null && typeDisplayName.contains(s11))
						s12 = s11.replaceAll("\\s", "").toUpperCase();
					String s13 = (String) queueSetByType.get((new StringBuilder()).append(s12).append(".")
							.append(integer).append(".").append(integer1).toString());
					if (VERBOSE) {
						System.out.println((new StringBuilder()).append(
								"com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Authoring Application :")
								.append(s12).toString());
						System.out.println((new StringBuilder()).append("*** ").append(s13).append(" ***").toString());
					}
					if (s13 == null) {
						s13 = (String) queueSetByType.get((new StringBuilder()).append(s12).append(".").append(integer)
								.append(".").append("0").toString());
						if (s13 == null) {
							s13 = (String) queueSetByType.get((new StringBuilder()).append(s12).append(".").append("0")
									.append(".").append(integer1).toString());
							if (s13 == null)
								s13 = (String) queueSetByType.get((new StringBuilder()).append(s12).append(".")
										.append("0").append(".").append("0").toString());
						}
					}
					if (queueSets.contains(s13)) {
						if (VERBOSE)
							System.out.println((new StringBuilder()).append(
									"com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet : Name of Queue Set: ")
									.append(s13).toString());
						as[1] = s13;
					}
				}
			}
		} catch (Exception exception) {
			if (VERBOSE)
				System.out.println(
						"com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet() ERROR Impossible to get the Authoring Application");
			exception.printStackTrace();
		}
		if (VERBOSE)
			System.out.println("com.ptc.wvs.util.queues.PublishJobUtil.filterQueueSet() END");
		return as;
	}

	private static final String CLASS_NAME = "com.ptc.wvs.util.queues.PublishJobUtil";
	private static final String QUEUE_SET_NAME = "publish.publishqueue.priorities.filtermethod.queueSetName.";
	private static final String SEP = ".";
	private static final String DEFAULT = "0";
	private static boolean VERBOSE = false;
	private static String IBA_IDENTIFIER = "typeDisplayName";
	private static Vector queueSets;
	private static Vector typeDisplayName;
	private static Hashtable queueSetByType;

	static {
		queueSets = new Vector();
		typeDisplayName = new Vector();
		queueSetByType = new Hashtable();
		try {
			String s = WVSProperties.getPropertyValue("com.ptc.wvs.util.queues.PublishJobUtil.verbose");
			if (s != null && s.equalsIgnoreCase("TRUE"))
				VERBOSE = true;
		} catch (Throwable throwable) {
			System.out.println("com.ptc.wvs.util.queues.PublishJobUtil: Error reading properties ");
			throwable.printStackTrace();
		}
		try {
			String s1 = WVSProperties.getPropertyValue("publish.publishqueue.setnames");
			if (s1 != null && s1.trim().length() > 0) {
				StringTokenizer stringtokenizer = new StringTokenizer(s1.trim(), " ");
				do {
					if (!stringtokenizer.hasMoreTokens())
						break;
					String s4 = stringtokenizer.nextToken();
					boolean flag = true;
					int i = 0;
					do {
						if (i >= s4.length())
							break;
						if (Character.getType(s4.charAt(i)) != 1) {
							flag = false;
							break;
						}
						i++;
					} while (true);
					if (flag)
						queueSets.add(s4);
				} while (true);
			}
			String s2 = WVSProperties.getPropertyValue("publish.publishqueue.priorities.filtermethod.typeDisplayName");
			if (s2 != null && s2.trim().length() > 0) {
				String s6;
				for (StringTokenizer stringtokenizer1 = new StringTokenizer(s2.trim(), ","); stringtokenizer1
						.hasMoreTokens(); typeDisplayName.add(s6))
					s6 = stringtokenizer1.nextToken();

			}
		} catch (Throwable throwable1) {
			System.out.println(
					"com.ptc.wvs.util.queues.PublishJobUtil: Error reading properties publish.publishqueue.setnames");
			throwable1.printStackTrace();
		}
		try {
			Enumeration enumeration = WVSProperties.getProperties().keys();
			do {
				if (!enumeration.hasMoreElements())
					break;
				String s3 = (String) enumeration.nextElement();
				if (s3.startsWith("publish.publishqueue.priorities.filtermethod.queueSetName.")) {
					String s5 = WVSProperties.getPropertyValue(s3);
					if (s5 != null) {
						s5 = s5.toUpperCase();
						s3 = s3.substring("publish.publishqueue.priorities.filtermethod.queueSetName.".length());
						queueSetByType.put(s3, s5);
					}
				}
			} while (true);
		} catch (Throwable throwable2) {
			System.out.println(
					"com.ptc.wvs.util.queues.PublishJobUtil: Error reading properties publish.publishqueue.priorities.filtermethod.queueSetName.");
			throwable2.printStackTrace();
		}
	}
}
