package org.onehippo.threestep.cms.workflow.scxml;

import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.model.ModelException;
import org.onehippo.repository.documentworkflow.Request;
import org.onehippo.repository.documentworkflow.action.AbstractDocumentTaskAction;

/**
 * @version "\$Id$" kenan
 */
public class AcceptReviewAction extends AbstractDocumentTaskAction<AcceptReviewTask> {

    private static final long serialVersionUID = 1L;



    @SuppressWarnings("unused")
    public void setRequestExpr(String requestExpr) {
        setParameter("requestExpr", requestExpr);
    }

    public String getRequestExpr() {
        return getParameter("requestExpr");
    }

    @SuppressWarnings("unused")
    public void setReviewer(String requestExpr) {
        setParameter("reviewer", requestExpr);
    }

    public String getReviewer() {
        return getParameter("reviewer");
    }

    @Override
    protected AcceptReviewTask createWorkflowTask() {
        return new AcceptReviewTask();
    }

    @Override
    protected void initTask(AcceptReviewTask task) throws ModelException, SCXMLExpressionException {
        super.initTask(task);
        task.setRequest((Request) eval(getRequestExpr()));
        task.setReviewer((String) eval(getReviewer()));
    }
}
