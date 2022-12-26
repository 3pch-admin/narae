package ext.narae.schedule;

import java.sql.Timestamp;
import java.util.Hashtable;

import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.queue.MethodArgument;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.scheduler.PrimitiveArg;
import wt.scheduler.ScheduleItem;
import wt.scheduler.ScheduleMethodArg;
import wt.scheduler.SchedulingHelper;
import wt.util.WTException;

public class EMailScheduler {

	private static final String shceduleQueueName = "E3psEmailScheduleQueue";
	private static final String processQueueName = "E3psEmailProcessQueue";
	private static final String targetClassName = "com.e3ps.common.mail.MailFTP";
	private static final String targetMethodName = "sendMail";

	public static void createScheduleItem(Hashtable ht) throws WTException {

		ScheduleItem item = ScheduleItem.newScheduleItem();
		item.setQueueName(shceduleQueueName);
		item.setTargetClass(targetClassName);

		// The first method argument is a String
		PrimitiveArg arg1 = PrimitiveArg.newPrimitiveArg();
		arg1.setSequenceNumber(0);
		arg1.setArgVal(new MethodArgument(Hashtable.class, ht));
		ScheduleMethodArg[] m_args = null;
		m_args = new ScheduleMethodArg[1];
		m_args[0] = arg1;

		item.setTargetMethod(targetMethodName);
		item.setStartDate(new Timestamp(System.currentTimeMillis()));
		item.setToBeRun(1);
		item.setPeriodicity(1);
		item.setItemName("EMailSender");
		item.setItemDescription("EMailSender");

		item = SchedulingHelper.service.addItem(item, m_args);
	}

	public static void createProcessItem(Hashtable ht) throws WTException {
		WTPrincipal principal = OrganizationServicesHelper.manager.getUser("wcadmin");
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		if (queue == null) {
			throw new WTException(
					"PLEASE MAKE THE QUEUE [" + processQueueName + "] WITH QUEUE MANAGER OF WINDCHILL!!!");
		}

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { ht };

		queue.addEntry(principal, targetMethodName, targetClassName, argClasses, argObjects);
	}

}
