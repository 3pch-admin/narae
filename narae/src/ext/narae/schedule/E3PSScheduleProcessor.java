package ext.narae.schedule;

import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.ptc.wvs.server.schedule.Schedulable;
import com.ptc.wvs.server.schedule.ScheduledJobProcessor;

import wt.log4j.LogR;

public class E3PSScheduleProcessor extends ScheduledJobProcessor {

	private static final Logger logger = LogR.getLoggerInternal(E3PSScheduleProcessor.class.getName());
	private static final String JOB_ERP_STATE = "ERP_RECEIVE_STATE_DAILY_BATCH";

	public E3PSScheduleProcessor(String arg0) {
		super(arg0);

	}

	@Override
	protected void doScheduleJob(Schedulable arg0, boolean arg1, String arg2, Properties arg3) {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter doScheduleJob()");
			logger.debug("   _schedulable=" + arg0);
			logger.debug("   _listOnly=" + arg1);
			logger.debug("   _containerRef=" + arg2);
		}
		if ("ERP_RECEIVE_STATE_DAILY_BATCH".equals(arg0.getIdentifier())) { // 프로젝트 일배치 23:00
			System.out.println("E3PS JOB Schedule START ");
			E3PSScheduleJobs.erpReceiveStateDailyBatch();
			System.out.println("E3PS JOB Schedule END ");
		}
		if ("RESET_QUEUE_DAILY_BATCH".equals(arg0.getIdentifier())) { // 프로젝트 일배치 23:00
			System.out.println("E3PS JOB Schedule START ");
			E3PSScheduleJobs.resetQueueDailyBatch();
			System.out.println("E3PS JOB Schedule END ");
		}

	}

}