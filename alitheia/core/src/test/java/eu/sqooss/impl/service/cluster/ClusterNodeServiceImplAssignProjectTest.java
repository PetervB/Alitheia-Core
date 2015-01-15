package eu.sqooss.impl.service.cluster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

public class ClusterNodeServiceImplAssignProjectTest {

	private static ClusterNodeServiceImpl impl;
	private static Logger logger;
	private static BundleContextMockImpl bundleContext;
	
	@BeforeClass
    public static void setUp() {
		logger = mock(Logger.class);
		bundleContext = new BundleContextMockImpl();
		new AlitheiaCore(bundleContext);
        impl = new ClusterNodeServiceAssignProjectImpl();
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
    
    @Test(expected = ClusterNodeActionException.class)
    public void testAssignProjectStringNoProject() throws ClusterNodeActionException {
    	impl.assignProject("test");
    }
    
    @Test(expected = ClusterNodeActionException.class)
    public void testAssignProjectStoredProjectNull() throws ClusterNodeActionException {
    	impl.assignProject(impl.getThisNode(), null);
    }
    
    @Test(expected = ClusterNodeActionException.class)
    public void testAssignProjectNodeNull() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	impl.assignProject(null, storedProject);
    }
    
    @Test
    public void testAssignProjectStoredProject() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	assertTrue(impl.assignProject(storedProject));
    }
    
    @Test
    public void testIsProjectAssigned() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	assertFalse(impl.isProjectAssigned(storedProject));
    	impl.assignProject(storedProject);
    	storedProject.setClusternode(impl.getThisNode());
    	assertTrue(impl.isProjectAssigned(storedProject));
    }
    
    @Test
    public void testAssignAssignedProject() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	assertFalse(impl.isProjectAssigned(storedProject));
    	impl.assignProject(storedProject);
    	storedProject.setClusternode(impl.getThisNode());
    	assertTrue(impl.isProjectAssigned(storedProject));
    	assertTrue(impl.assignProject(storedProject));
    }
    
    @Test
    public void testAssignAssignedProjectToNewNode() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	assertFalse(impl.isProjectAssigned(storedProject));
    	impl.assignProject(storedProject);
    	storedProject.setClusternode(impl.getThisNode());
    	assertTrue(impl.isProjectAssigned(storedProject));
    	ClusterNode clusterNode = new ClusterNode("Extra Node");
    	clusterNode.setProjects(new HashSet<StoredProject>());
    	clusterNode.setId(42);
		assertTrue(impl.assignProject(clusterNode, storedProject));
    }
    
    @Test(expected = ClusterNodeActionException.class)
    public void testAssignAssignedProjectToFaultyNode() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	ClusterNode clusterNode = new ClusterNode("Extra Node");
    	impl.assignProject(clusterNode, storedProject);
    }
    
    @Test
    public void testAssignProjectStoredProjectWithNode() throws ClusterNodeActionException {
    	StoredProject storedProject = new StoredProject("test");
    	assertTrue(impl.assignProject(impl.getThisNode(), storedProject));
    }
}
