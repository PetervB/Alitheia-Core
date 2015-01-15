package eu.sqooss.impl.service.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

public class ClusterNodeServiceImplResponseTest {

	private StringWriter stringWriter;
	
	private static ClusterNodeServiceImpl impl;
	private static Logger logger;
	private static BundleContextMockImpl bundleContext;
	
	@BeforeClass
    public static void setUp() {
		logger = mock(Logger.class);
		bundleContext = new BundleContextMockImpl();
		new AlitheiaCore(bundleContext);
        impl = new ClusterNodeServiceResponseImpl();
		impl.setInitParams(bundleContext, logger);
        impl.startUp();
        // Initialize the project set
        impl.getThisNode().setProjects(new HashSet<StoredProject>());
        impl.getClusterNodeName();
    }
	
	@AfterClass
	public static void shutDown() {
		impl.shutDown();
	}

	@Test
    public void testClusterNodeServiceImpl() {
        assertNotNull(impl);
    }

    @Test
    public void testInitialDoGet() throws ServletException, IOException {
    	HttpServletRequest request = initialRequestSetup();
    	HttpServletResponse response = initialResponseSetup();
        impl.doGet(request, response);
        assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"400\">Unknown action</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithBadAction() throws ServletException, IOException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("bad action");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"501\">Bad action [bad action]</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithNullAction() throws ServletException, IOException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn(null);
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"400\">Unknown action</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithActionAssignProject() throws ServletException, IOException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("ASSIGN_PROJECT");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"404\">Project null not found</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithActionAssignProjectWithProjectAndIdFaultyId() throws ServletException, IOException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("ASSIGN_PROJECT");
    	when(request.getParameter("projectname")).thenReturn("test");
    	when(request.getParameter("projectid")).thenReturn("poiasejtlkasjeps");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"400\">Invalid projectid [poiasejtlkasjeps]</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithActionAssignProjectWithProjectAndId() throws ServletException, IOException, ClusterNodeActionException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("ASSIGN_PROJECT");
    	when(request.getParameter("projectname")).thenReturn("test");
    	when(request.getParameter("projectid")).thenReturn("42");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"404\">Project with id:42 not found</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test(expected = NullPointerException.class)
    public void testDoGetWithActionGetAssignedProjects() throws ServletException, IOException, ClusterNodeActionException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("GET_ASSIGNED_PROJECTS");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithActionGetAssignedProjectsWithNode() throws ServletException, IOException, ClusterNodeActionException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("GET_ASSIGNED_PROJECTS");
    	when(request.getParameter("clusternode")).thenReturn("testNode");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result/>\n<status code=\"404\">ClusterNode testNode not found</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }
    
    @Test
    public void testDoGetWithActionGetKnownServers() throws ServletException, IOException, ClusterNodeActionException {
    	HttpServletRequest request = initialRequestSetup();
    	when(request.getParameter("action")).thenReturn("GET_KNOWN_SERVERS");
    	HttpServletResponse response = initialResponseSetup();
    	impl.doGet(request, response);
    	assertEquals("<?xml version=\"1.0\"?>\n<sqo-oss-response service=\"clusternode\">\n<result></result>\n<status code=\"200\">Clusternode list processed succesfuly</status>\n</sqo-oss-response>", stringWriter.toString().trim());
    }

	private HttpServletRequest initialRequestSetup() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		return request;
	}

	private HttpServletResponse initialResponseSetup() throws IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);
		return response;
	}

}
