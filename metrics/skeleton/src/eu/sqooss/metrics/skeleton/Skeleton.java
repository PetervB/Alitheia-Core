/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
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

/*
** That copyright notice makes sense for SQO-OSS paricipants but
** not for everyone. For the Squeleton plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the Squeleton
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string 'metrics'
** because this is hard-coded in parts of the Alitheia core.
*/
package eu.sqooss.metrics.skeleton;

import java.util.List;

import org.osgi.framework.BundleContext;

/* These are imports of standard Alitheia core services and types.
** You are going to need these anyway; some others that you might
** need are the FDS and other Metric interfaces, as well as more
** DAO types from the database service.
*/
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;


public class Skeleton extends AbstractMetric implements ProjectFileMetric {
    
    public Skeleton(BundleContext bc) {
        super(bc);        
 
        /*Tells the metric activator when to call this metric*/
        super.addActivationType(ProjectFile.class);
        
        /*Tells the UI what it metric is calculated against*/
        super.addMetricActivationType("SKEL", ProjectFile.class);
    }
    
    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    this.getDescription(),
                    "SKEL",
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        //Return a list of ResultEntries by querying the DB for the measurements
        //implement by the supported metric and calculated for the specific 
        //project file
        return null;
    }
    
    public void run(ProjectFile a) {
        //1. Get stuff related to the provided project file
        //2. Calculate one or more numbers
        //3. Store a result to the database
    }

    
}

// vi: ai nosi sw=4 ts=4 expandtab

