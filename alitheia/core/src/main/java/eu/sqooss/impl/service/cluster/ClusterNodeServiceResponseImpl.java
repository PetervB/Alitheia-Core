package eu.sqooss.impl.service.cluster;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService.ClusterNodeAction;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.StoredProject;

public class ClusterNodeServiceResponseImpl extends ClusterNodeServiceImpl {

	private static final long serialVersionUID = 1L;

	private String createXMLResponse(String resultMessage, String statusMessage, int statusCode) {
	    StringBuilder s = new StringBuilder();
	    s.append("<?xml version=\"1.0\"?>\n");
	    s.append("<sqo-oss-response service=\"clusternode\">\n");
	    if (resultMessage!=null) {
	        s.append("<result>" + resultMessage + "</result>\n");
	    } else {
	        s.append("<result/>\n");
	    }
	    s.append("<status code=\"" + String.valueOf(statusCode) + "\"");
	    if (statusMessage!=null) {
	        s.append(">" + statusMessage + "</status>\n");
	    } else {
	        s.append("/>\n");
	    }
	    s.append("</sqo-oss-response>\n");
		return s.toString();
	}

	private void sendXMLResponse(HttpServletResponse response, int status, String content)
			throws ServletException, IOException {
			    response.setStatus(status);
				response.setContentType("text/xml;charset=UTF-8");
				response.getWriter().println(content);
				response.flushBuffer();
			}

	/**
	 * This is the standard HTTP request handler. It maps GET parameters based on 
	 * the mandatory 'action' parameter to misc internal processes.
	 *
	 * The response codes in HTTP are used as follows:
	 * - SC_OK  if the requested action succeeds
	 * - SC_BAD_REQUEST (400) if the request is syntactically incorrect, which in
	 *          this case means that one of the required parameter "action"
	 *          is missing, or projectid is not a long integer.
	 * - SC_NOT_FOUND (404) if the project or clusternode does not exist in the database.
	 * - SC_NOT_IMPLEMENTED if the action type is not supported
	 */
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
				
				String requestedAction = request.getParameter("action");
			
				String projectname = request.getParameter("projectname");
			    String projectid = request.getParameter("projectid");
			    String clusternode = request.getParameter("clusternode");
				
			    String content;          // holder of complete response output
			    StringBuilder bcontent;  // holder of complete response output
			    
			    StoredProject project;
			    ClusterNode node;
			    
			    // ERROR if no action requested
			    if (requestedAction == null) {
			    	content=createXMLResponse(null, "Unknown action",HttpServletResponse.SC_BAD_REQUEST);
			    	sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST,content); 
			       return;
			    }
			    
			    // ERROR if unknown action requested
			    ClusterNodeAction action = null;
			    try {
			        action = ClusterNodeAction.valueOf(requestedAction.toUpperCase());
			    } catch (IllegalArgumentException e) {
			        String errorMessage = "Bad action [" + requestedAction + "]";
			        logger.warn(errorMessage);
			        content=createXMLResponse(null, errorMessage,HttpServletResponse.SC_NOT_IMPLEMENTED);
			        sendXMLResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, content);
			        return;
			    }
			    
			      
			    // Perform Actions
			    switch (action){
			     case ASSIGN_PROJECT :
			    	 // valid parameters:
			    	 // projectname : Name of the project to assign.  
			    	 // projectid   : ID of the project to assign. 
			    	 //               Used ONLY if projectname parameter is missing, or projectname not found
			    	 // clusternode : The Clusternode name to which the project will be assigned
			    	 //               If empty, assign it to this clusternode
			    	 // Example: http://localhost:8088/clusternode?action=assign_project&projectname=iTALC&clusternode=sqoserver1
			
			    	 dbs.startDBSession();
			     	 project = StoredProject.getProjectByName(projectname);
			     	 dbs.rollbackDBSession();
			     	 if (project==null) {
			     		 if (projectid!=null)  {
			                 long id = 0;
			                 try {
			                	 id = Long.valueOf(projectid);
			                 } catch (Exception ex){
			          	    	 content=createXMLResponse(null,"Invalid projectid [" + projectid + "]", HttpServletResponse.SC_BAD_REQUEST);
			        	    	 sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST, content);
			        	    	 break;                   	 
			                 }
			                 dbs.startDBSession();
			        		 project = dbs.findObjectById(StoredProject.class, id);
			        		 dbs.rollbackDBSession();
			        		 if (project==null) {
			           	    	content = createXMLResponse(null,"Project with id:" + projectid + " not found", HttpServletResponse.SC_NOT_FOUND);
			        	    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
			        	    	break;                   	             			 
			        		 }
			     	     } else {
			     	    	content = createXMLResponse(null,"Project " + projectname + " not found", HttpServletResponse.SC_NOT_FOUND);
			    	    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
			    	    	break;
			     	     }
			     	 }
			     	 
			     	 if (clusternode==null) {
			     	     node = thisNode;	 
			     	 } else {
			     	     dbs.startDBSession();
			     	     node = ClusterNode.getClusteNodeByName(clusternode);
			     	     dbs.rollbackDBSession();
			     	     if (node==null) {
			                 content = createXMLResponse(null,"ClusterNode " + clusternode + " not found", HttpServletResponse.SC_NOT_FOUND);
			                 sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
			                 break;         	         
			     	     }
			     	 }
			    	 try {
			    	     if (assignProject(node,project)){
			    	    	content = createXMLResponse(null, "Project " + project.getName() + " assigned to " + node.getName(), HttpServletResponse.SC_OK);
			    	    	sendXMLResponse(response, HttpServletResponse.SC_OK, content);       	    	
			    	     }        	     
			    	 } catch (ClusterNodeActionException ex) {
			 	    	content = createXMLResponse(null, ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
				    	sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST, content);        		 
			    	 }     
			    	 break;
			     case GET_ASSIGNED_PROJECTS:
			    	 // valid parameters:
			    	 // clusternode : The Clusternode name to query for
			    	 //               If empty, assign it to this clusternode
			    	 // Example: http://localhost:8088/clusternode?action=get_assigned_projects&clusternode=sqoserver1
			
			         // TODO: Clustering - Extract interface  
			         if (clusternode==null) {
			       	     node = thisNode;	 
			       	 } else {
			       	     dbs.startDBSession();
			       	     node = ClusterNode.getClusteNodeByName(clusternode);
			       	     dbs.rollbackDBSession();
			       	 }
			     	 if (node==null){
			  	    	content = createXMLResponse(null, "ClusterNode "+clusternode+" not found", HttpServletResponse.SC_NOT_FOUND);
				    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
				    	break;
			     	 }
			     	          	 
			         bcontent = new StringBuilder();
			         dbs.startDBSession();
			         Set<StoredProject> assignments = ClusterNode.thisNode().getProjects();
			         if ((assignments!=null) &&  (assignments.size()>0) ){
			             bcontent.append("\n");
			             for (StoredProject sp : assignments) {                
			                 bcontent.append("<project id=\"" + sp.getId() + "\"");
			                                      // check if project is currently being updated
			                 // yes/no/unknown, (unknown means that this project is assigned to another clusternode instance)                    
			                 bcontent.append(">" + sp.getName() + "</project>\n");
			             }
			         }
			         dbs.rollbackDBSession();
			         content = createXMLResponse(bcontent.toString(), "Project list processed succesfuly", HttpServletResponse.SC_OK);
			         sendXMLResponse(response, HttpServletResponse.SC_OK, content);
			    	 break;
			     case GET_KNOWN_SERVERS:
			         // valid parameters: No need for parameters!
			         // Example: http://localhost:8088/clusternode?action=get_known_servers
			         bcontent = new StringBuilder();
			         dbs.startDBSession();
			         List<ClusterNode> nodes = (List<ClusterNode>) dbs.doHQL("FROM ClusterNode",null);
			         if ((nodes!=null) &&  (nodes.size()>0) ){
			             bcontent.append("\n");
			             for (ClusterNode cn : nodes) {                
			                 bcontent.append("<clusternode id=\"" + cn.getId() + "\">" + cn.getName() + "</clusternode>\n");
			             }
			         }
			         dbs.rollbackDBSession();
			         content = createXMLResponse(bcontent.toString(), "Clusternode list processed succesfuly", HttpServletResponse.SC_OK);
			         sendXMLResponse(response, HttpServletResponse.SC_OK, content);
			    	 break;
			     default:
			    	 // you shouldn't be here! - implement missing actions!
			    	 
			    }
			}

}
