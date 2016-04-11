package org.onehippo.repository.documentworkflow;

import org.onehippo.threestep.cms.workflow.ReviewWorkflow;
import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.scxml.SCXMLWorkflowContext;
import org.onehippo.repository.scxml.SCXMLWorkflowExecutor;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * @version "\$Id$" kenan
 */
public class DocumentReviewWorkflowImpl extends DocumentWorkflowImpl implements ReviewWorkflow {


    public DocumentReviewWorkflowImpl() throws RemoteException {
    }

    @Override
    public void requestReview() throws WorkflowException {
        getWorkflowExecutor().start();
        getWorkflowExecutor().triggerAction("requestReview");
    }

    @Override
    public void requestPublicationReview(final Date publicationDate) throws WorkflowException {
        getWorkflowExecutor().start();
        getWorkflowExecutor().triggerAction("requestReview", createPayload("targetDate", publicationDate));
    }

    @Override
    public void acceptReview(String requestIdentifier) throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("acceptReview", getRequestActionActions(requestIdentifier, "acceptReview"),
                createPayload("request", executor.getData().getRequests().get(requestIdentifier)));
    }

    @Override
    public void rejectReview(String requestIdentifier, String reason) throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("rejectReview", getRequestActionActions(requestIdentifier, "rejectReview"),
                createPayload("request", executor.getData().getRequests().get(requestIdentifier), "reason", reason));
    }

}
