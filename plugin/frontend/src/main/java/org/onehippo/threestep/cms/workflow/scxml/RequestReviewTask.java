package org.onehippo.threestep.cms.workflow.scxml;

import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.DocumentVariant;
import org.onehippo.repository.documentworkflow.ReviewRequest;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.RepositoryException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * @version "\$Id$" kenan
 */
public class RequestReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private String type;
    private String threeStepType;
    private DocumentVariant contextVariant;
    private Date targetDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThreeStepType() {
        return threeStepType;
    }

    public void setThreeStepType(String threeStepType) {
        this.threeStepType = threeStepType;
    }

    public DocumentVariant getContextVariant() {
        return contextVariant;
    }

    public void setContextVariant(DocumentVariant contextVariant) {
        this.contextVariant = contextVariant;
    }

    @SuppressWarnings("unused")
    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public Object doExecute() throws WorkflowException, RepositoryException, RemoteException {

        DocumentHandle dm = getDocumentHandle();

        if (!dm.isRequestPending()) {

            if (targetDate == null) {
                new ReviewRequest(getType(), getThreeStepType(), contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession()),
                        contextVariant, getWorkflowContext().getUserIdentity());
            } else {
                new ReviewRequest(getType(), getThreeStepType(), contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession()),
                        contextVariant, getWorkflowContext().getUserIdentity(), targetDate);
            }

            getWorkflowContext().getInternalWorkflowSession().save();
        } else {
            throw new WorkflowException("publication request already pending");
        }

        return null;
    }

}
