/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.cluster;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

/**
 * @author George M. Zouganelis
 *
 */
public abstract class ClusterNodeServiceImpl extends HttpServlet {
    private static final long serialVersionUID = 1L;
	static final String localServerName;
	static{
		String hostname;
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			hostname = localMachine.getHostName();
		}
		catch(java.net.UnknownHostException ex) {
    		// TODO: Clustering - Implement a hashing algorithm for unique server identification
			hostname = "unknown host";
		} 		
		localServerName = hostname;
			
	}

    protected Logger logger = null;
    private AlitheiaCore core = null;
    private HttpService httpService = null;
    private BundleContext context;
    protected DBService dbs = null;
    
    protected ClusterNode thisNode = null;

    public ClusterNodeServiceImpl() {}
    
    public String getClusterNodeName(){
	   return thisNode.getName();
    }
    
    /**
     * This is visible for testing, but by lack of guava we can't use
     * the @visiblefortesting annotation.
     */
    public ClusterNode getThisNode() {
    	return thisNode;
    }
    
	public void setInitParams(BundleContext bc, Logger l) {
		this.context = bc;
		this.logger = l;
	}

	public void shutDown() {}

	public boolean startUp() {
		
		/* Get a reference to the core service*/
        ServiceReference serviceRef = null;
      
        core = AlitheiaCore.getInstance();
        dbs = core.getDBService();
        if (logger != null) {
            logger.info("Got a valid reference to the logger");
        } else {
            System.out.println("ERROR: ClusteNodeService got no logger");
        }

        /* Get a reference to the HTTP service */
        serviceRef = context.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceRef != null) {
            httpService = (HttpService) context.getService(serviceRef);
           	try {
				httpService.registerServlet("/clusternode", (Servlet) this, null, null);
			} catch (ServletException e) {
				logger.error("Cannot register servlet to path /clusternode");
				return false;
			} catch (NamespaceException e) {
				logger.error("Duplicate registration at path /clusternode");
				return false;
			}
        } else {
            logger.error("Could not load the HTTP service.");
        }
        logger.info("Succesfully started clusternode service");

		
		// At this point, this ClusterNode has not been registered to the
		// database yet, so do it!
		if (thisNode == null) { // paranoia check
			dbs.startDBSession();
			// Check if previously registered in DB
			Map<String, Object> serverProps = new HashMap<String, Object>(1);
			serverProps.put("name", localServerName);
			List<ClusterNode> s = dbs.findObjectsByProperties(
					ClusterNode.class, serverProps);

			if (s.isEmpty()) {
				// not registered yet, create a record in DB
				thisNode = new ClusterNode();
				thisNode.setName(localServerName);
				if (!dbs.addRecord(thisNode)) {
					logger.error("Failed to register ClusterNode <"
							+ localServerName + ">");
					dbs.rollbackDBSession();
					return false;
				} else {
					dbs.commitDBSession();
					logger.info("ClusterNode <" + localServerName
							+ "> registered succesfully.");
					return true;
				}
			} else {
				// already registered, keep the record from DB
				dbs.rollbackDBSession();
				thisNode = s.get(0);
				logger.info("ClusterNode <" + localServerName
						+ "> registered succesfully.");
			}
		}
		return true;
	}

	public boolean assignProject(String projectname) throws ClusterNodeActionException {
		// Default returns false, can be overwritten by subclass
		return false;
	}

	public boolean assignProject(ClusterNode node, StoredProject project) throws ClusterNodeActionException {
		// Default returns false, can be overwritten by subclass
		return false;
	}

	public boolean assignProject(StoredProject project) throws ClusterNodeActionException {
		// Default returns false, can be overwritten by subclass
		return false;
	}

	public boolean isProjectAssigned(StoredProject project) {
		// Default returns false, can be overwritten by subclass
		return false;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Default does nothing, can be overwritten by subclass
	}
}
