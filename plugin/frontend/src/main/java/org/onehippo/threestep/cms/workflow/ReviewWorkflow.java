package org.onehippo.threestep.cms.workflow;

import org.hippoecm.repository.api.Workflow;
import org.hippoecm.repository.api.WorkflowException;

import java.util.Date;

/**
 * @version "\$Id$" kenan
 */
public interface ReviewWorkflow extends Workflow {

    /**
     * Request document review for the reviewer role before publication.
     * @throws WorkflowException
     */
    void requestReview() throws WorkflowException;

    /**
     * Request a scheduled publication review for the reviewer role.
     * @param publicationDate
     * @throws WorkflowException
     */
    void requestPublicationReview(final Date publicationDate) throws WorkflowException;

    /**
     * Accept a review request.
     * @param requestIdentifier
     * @throws WorkflowException
     */
    void acceptReview(String requestIdentifier) throws WorkflowException;

    /**
     * Reject a review request plus give reason to author why.
     * @param requestIdentifier
     * @param reason
     * @throws WorkflowException
     */
    void rejectReview(String requestIdentifier, String reason) throws WorkflowException;



}
