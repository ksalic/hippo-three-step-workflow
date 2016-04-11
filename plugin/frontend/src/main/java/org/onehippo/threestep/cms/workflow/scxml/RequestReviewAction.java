package org.onehippo.threestep.cms.workflow.scxml;

import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.model.ModelException;
import org.onehippo.repository.documentworkflow.DocumentVariant;
import org.onehippo.repository.documentworkflow.action.AbstractDocumentTaskAction;

import java.util.Date;

/**
 * @version "\$Id$" kenan
 */
public class RequestReviewAction extends AbstractDocumentTaskAction<RequestReviewTask> {

    private static final long serialVersionUID = 1L;

    public String getThreeStepType() {
        return getParameter("threeStepType");
    }

    @SuppressWarnings("unused")
    public void setThreeStepType(String type) {
        setParameter("threeStepType", type);
    }

    public String getType() {
        return getParameter("type");
    }

    @SuppressWarnings("unused")
    public void setType(String type) {
        setParameter("type", type);
    }

    public String getContextVariantExpr() {
        return getParameter("contextVariantExpr");
    }

    @SuppressWarnings("unused")
    public void setContextVariantExpr(String contextVariantExpr) {
        setParameter("contextVariantExpr", contextVariantExpr);
    }

    public String getTargetDateExpr() {
        return getParameter("targetDateExpr");
    }

    @SuppressWarnings("unused")
    public void setTargetDateExpr(String targetDateExpr) {
        setParameter("targetDateExpr", targetDateExpr);
    }

    @Override
    protected RequestReviewTask createWorkflowTask() {
        return new RequestReviewTask();
    }

    @Override
    protected void initTask(RequestReviewTask task) throws ModelException, SCXMLExpressionException {
        super.initTask(task);
        task.setType(getType());
        task.setThreeStepType(getThreeStepType());
        task.setContextVariant((DocumentVariant) eval(getContextVariantExpr()));
        task.setTargetDate((Date) eval(getTargetDateExpr()));
    }
}

