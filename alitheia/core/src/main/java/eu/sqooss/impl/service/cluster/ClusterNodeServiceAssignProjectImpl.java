package eu.sqooss.impl.service.cluster;

import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.StoredProject;

public class ClusterNodeServiceAssignProjectImpl extends ClusterNodeServiceImpl implements ClusterNodeService {

	private static final long serialVersionUID = 1L;

	/**
	 * Assign a StoredProject to a ClusterNode
	 * Reasonable causes of failure:
	 *  1.NULL passed server
	 *  2.NULL passed project
	 *  3.Assignment is locked (server is working on project)
	 *  
	 * @param node the cluster node target
	 * @param project stored project to assign 
	 * @return
	 */
	@Override
	public boolean assignProject(ClusterNode node, StoredProject project)
			throws ClusterNodeActionException {
				// check if valid server passed
			    if (node==null) {
					throw new ClusterNodeActionException("Request to assign a project to a null clusternode");
				}
				// check if valid project passed
				if (project==null) {
					throw new ClusterNodeActionException("Request to assign a null project to a clusternode");
				}
			
			    String projectName = project.getName();
				String nodeName = node.getName();
				try {          
			    	// check if project is already assigned to any ClusterNode
			        ClusterNode assignment = project.getClusternode();
			        if (assignment == null) {
			            // new project assignment
			            logger.info("Assigning project " + projectName + " to "
			                    + nodeName);
			            node.getProjects().add(project);
			        } else {
			            logger.info("Moving project " + projectName + " from "
			                    + assignment.getName() + " to "
			                    + nodeName);
			            if (assignment.getId() == node.getId()) {
			                logger.info("No need to move " + projectName
			                        + " - Already assigned!");
			                return true;
			            }
			        }
			    } catch (Exception e) {
			        throw new ClusterNodeActionException("Failed to assign project ["
			                + projectName + "] to clusternode [" + nodeName
			                + "]");
			    }
				return true;
			}

	/**
	 * Assign a StoredProject to this ClusterNode
	 * @param project project to assign
	 * @return  
	 */
	@Override
	public boolean assignProject(StoredProject project) throws ClusterNodeActionException {
		try {
			return assignProject(thisNode, project);
		} catch (ClusterNodeActionException ex) {
			throw ex;
		}
	}

	/**
	 * Overload for convenience. Use string instead of stored project.
	 * @param projectname project's name to assign
	 */
	@Override
	public boolean assignProject(String projectname) throws ClusterNodeActionException {
		dbs.startDBSession();
		StoredProject project = StoredProject.getProjectByName(projectname);
		dbs.rollbackDBSession();
	    if (project == null) {
	        //the project was not found, can't be assign
	    	String errorMessage = "The project [" + projectname + "] was not found"; 
	        logger.warn(errorMessage);
	        throw new ClusterNodeActionException(errorMessage);
	    }
	    try {
	        return assignProject(project);
	    } catch (ClusterNodeActionException ex) {
	    	throw ex;
	    }
	    
	}

	/**
	 * Check if a StoredProject is assigned to this ClusterNode
	 * @param project project to check
	 * @return  
	 */
	@Override
	public boolean isProjectAssigned(StoredProject project) {
	    return (project.getClusternode() != null);
	}
}
