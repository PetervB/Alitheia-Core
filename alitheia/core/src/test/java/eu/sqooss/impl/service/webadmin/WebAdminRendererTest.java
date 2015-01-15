package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.velocity.VelocityContext;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.cluster.BundleContextMockImpl;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerStats;

public class WebAdminRendererTest {

	private static BundleContextMockImpl bundleContext;
	private static WebAdminRenderer webAdminRenderer;
	
	@BeforeClass
    public static void setUp() {
		mock(Logger.class);
		bundleContext = new BundleContextMockImpl();
		new AlitheiaCore(bundleContext);
		webAdminRenderer = new WebAdminRenderer(bundleContext, new VelocityContext());
    }
	
	@Test
	public void testNoJobFailedStat() {
		SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
		Job[] jobs = new Job[0];
		when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
		SchedulerStats mockedSchedulerStats = mock(SchedulerStats.class);
		when(mockedScheduler.getSchedulerStats()).thenReturn(mockedSchedulerStats);
		when(mockedSchedulerStats.getFailedJobTypes()).thenReturn(new HashMap<String, Integer>());
		webAdminRenderer.setScheduler(mockedScheduler);
		String renderFailedJobs = webAdminRenderer.renderJobFailStats();
		assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Failed</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>No failures</td>\n\t\t\t<td>&nbsp;\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>", renderFailedJobs);
	}
	
	@Test
	public void testOneJobFailedStat() {
		SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
		Job[] jobs = new Job[0];
		when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
		SchedulerStats mockedSchedulerStats = mock(SchedulerStats.class);
		when(mockedScheduler.getSchedulerStats()).thenReturn(mockedSchedulerStats);
		HashMap<String, Integer> jobMap = new HashMap<String, Integer>();
		jobMap.put("test", 1);
		when(mockedSchedulerStats.getFailedJobTypes()).thenReturn(jobMap);
		webAdminRenderer.setScheduler(mockedScheduler);
		String renderFailedJobs = webAdminRenderer.renderJobFailStats();
		assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Failed</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>test</td>\n\t\t\t<td>1\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>", renderFailedJobs);
	}
	@Test
	public void testNoJobWaitStat() {
		SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
		Job[] jobs = new Job[0];
		when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
		SchedulerStats mockedSchedulerStats = mock(SchedulerStats.class);
		when(mockedScheduler.getSchedulerStats()).thenReturn(mockedSchedulerStats);
		when(mockedSchedulerStats.getWaitingJobTypes()).thenReturn(new HashMap<String, Integer>());
		webAdminRenderer.setScheduler(mockedScheduler);
		String renderFailedJobs = webAdminRenderer.renderJobWaitStats();
		assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Waiting</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>No failures</td>\n\t\t\t<td>&nbsp;\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>", renderFailedJobs);
	}
	
	@Test
	public void testOneJobWaitStat() {
		SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
		Job[] jobs = new Job[0];
		when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
		SchedulerStats mockedSchedulerStats = mock(SchedulerStats.class);
		when(mockedScheduler.getSchedulerStats()).thenReturn(mockedSchedulerStats);
		HashMap<String, Integer> jobMap = new HashMap<String, Integer>();
		jobMap.put("test", 1);
		when(mockedSchedulerStats.getWaitingJobTypes()).thenReturn(jobMap);
		webAdminRenderer.setScheduler(mockedScheduler);
		String renderFailedJobs = webAdminRenderer.renderJobWaitStats();
		assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Waiting</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>test</td>\n\t\t\t<td>1\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>", renderFailedJobs);
	}

	@Test
	public void testNoJobFailed() {
		SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
		Job[] jobs = new Job[0];
		when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
		webAdminRenderer.setScheduler(mockedScheduler);
		String renderFailedJobs = webAdminRenderer.renderFailedJobs();
		assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n<tr><td colspan=\"4\">No failed jobs.</td></tr>\t</tbody>\n</table>", renderFailedJobs);
	}
	
    @Test
    public void testOneJobFailed() {
    	SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
    	Job[] jobs = new Job[1];
    	jobs[0] = mock(Job.class);
    	when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
    	webAdminRenderer.setScheduler(mockedScheduler);
    	String renderFailedJobs = webAdminRenderer.renderFailedJobs();
    	assertEquals("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>" + jobs[0].toString() + "</td>\n\t\t\t<td><b>NA</b></td>\n\t\t\t<td><b>NA<b></td>\n\t\t\t<td><b>NA</b>\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>", renderFailedJobs);
    }
    
    @Test
    public void testOneJobFailedWithException() {
    	SchedulerServiceImpl mockedScheduler = mock(SchedulerServiceImpl.class);
    	Job[] jobs = new Job[1];
		jobs[0] = createJobWithException();
    	when(mockedScheduler.getFailedQueue()).thenReturn(jobs);
    	webAdminRenderer.setScheduler(mockedScheduler);
    	String renderFailedJobs = webAdminRenderer.renderFailedJobs();
    	
    	// No assert as the render will break on each insignificant change.
    }

	private Job createJobWithException() {
		return new Job() {
			
			public Exception getErrorException() {
				return new Exception("Test exception");
			}
			
			@Override
			protected void run() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long priority() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}
}
