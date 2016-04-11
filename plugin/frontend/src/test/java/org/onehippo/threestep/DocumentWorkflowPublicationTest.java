/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.threestep;

import org.junit.Test;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.onehippo.threestep.cms.workflow.ReviewWorkflow;

import javax.jcr.RepositoryException;

import static org.hippoecm.repository.api.HippoNodeType.HIPPO_REQUEST;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class DocumentWorkflowPublicationTest extends AbstractDocumentWorkflowIntegrationTest {

    @Test
    public void requestReviewAndCancelAsAuthor() throws Exception {
        assumeTrue(!isLive());
        final ReviewWorkflow documentReviewWorkflow = getDocumentReviewWorkflowAsAuthor(handle);
        documentReviewWorkflow.requestReview();
        assertTrue("Document has request", containsRequest());
        final DocumentWorkflow documentWorkflow = getDocumentWorkflowAsAuthor(handle);
        documentWorkflow.cancelRequest(getRequestId());
        assertFalse("Document doesn't have request anymore", containsRequest());
    }

    @Test
    public void requestReviewAsAuthorAndRejectAsReviewer() throws Exception {
        assumeTrue(!isLive());
        final ReviewWorkflow documentReviewWorkflow = getDocumentReviewWorkflowAsAuthor(handle);
        documentReviewWorkflow.requestReview();
        assertTrue("Document has request", containsRequest());
        final ReviewWorkflow documentWorkflow = getDocumentReviewWorkflowAsReviewer(handle);
        documentWorkflow.rejectReview(getRequestId(), "Horrible, true story bro!");
        assertTrue("Document has request", containsRequest());
        assertTrue("Document is not live", !isLive());

    }

    @Test
    public void requestReviewAsAuthorAndApproveAsReviewer() throws Exception {
        assumeTrue(!isLive());
        final ReviewWorkflow documentReviewWorkflow = getDocumentReviewWorkflowAsAuthor(handle);
        documentReviewWorkflow.requestReview();
        assertTrue("Document has request", containsRequest());
        final ReviewWorkflow documentWorkflow = getDocumentReviewWorkflowAsReviewer(handle);
        documentWorkflow.acceptReview(getRequestId());
        assertTrue("Document has request", containsRequest());
        assertTrue("Document is not live", !isLive());
    }

    @Test
    public void requestReviewAsAuthorAndApproveAsReviewerAndAcceptAsEditor() throws Exception {
        assumeTrue(!isLive());
        final ReviewWorkflow documentReviewWorkflow = getDocumentReviewWorkflowAsAuthor(handle);
        documentReviewWorkflow.requestReview();
        assertTrue("Document has request", containsRequest());
        final ReviewWorkflow documentReviewWorkflowReviewer = getDocumentReviewWorkflowAsReviewer(handle);
        documentReviewWorkflowReviewer.acceptReview(getRequestId());
        assertTrue("Document has request", containsRequest());
        final DocumentWorkflow documentWorkflow = getDocumentWorkflow(handle);
        documentWorkflow.acceptRequest(getRequestId());
        assertTrue("Document is live", isLive());
    }


    private boolean containsRequest() throws RepositoryException {
        return handle.hasNode(HIPPO_REQUEST);
    }

    private String getRequestId() throws RepositoryException {
        return handle.getNode(HIPPO_REQUEST).getIdentifier();
    }



}
