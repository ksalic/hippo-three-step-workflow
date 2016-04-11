package org.onehippo.threestep.cms.workflow.scxml;

import org.hippoecm.repository.HippoStdPubWfNodeType;
import org.onehippo.threestep.cms.workflow.ThreeStepNodeType;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.documentworkflow.Request;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.rmi.RemoteException;

/**
 * @version "\$Id$" kenan
 */
public class AcceptReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private Request request;
    private String reviewer;

    public Request getRequest() {
        return request;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    @Override
    public Object doExecute() throws WorkflowException, RepositoryException, RemoteException {
        final Session session = getWorkflowContext().getInternalWorkflowSession();
        Node requestNode = request.getCheckedOutNode(session);
        JcrUtils.ensureIsCheckedOut(requestNode.getParent());
        if(requestNode.isNodeType(ThreeStepNodeType.THREESTEP_REQUEST)){
            requestNode.setProperty(ThreeStepNodeType.THREESTEP_TYPE, "publish");
        }
        if(StringUtils.isNotEmpty(reviewer)){
            requestNode.setProperty(HippoStdPubWfNodeType.HIPPOSTDPUBWF_USERNAME, reviewer);
        }
        session.save();
        return null;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
}
