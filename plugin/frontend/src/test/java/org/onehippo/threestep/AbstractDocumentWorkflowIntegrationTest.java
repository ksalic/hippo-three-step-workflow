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

import org.hippoecm.repository.api.HippoWorkspace;
import org.hippoecm.repository.api.WorkflowManager;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.junit.Before;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.onehippo.repository.testutils.RepositoryTestCase;
import org.onehippo.threestep.cms.workflow.ReviewWorkflow;

import javax.jcr.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.hippoecm.repository.HippoStdNodeType.*;
import static org.hippoecm.repository.HippoStdPubWfNodeType.*;
import static org.hippoecm.repository.api.HippoNodeType.*;
import static org.onehippo.repository.util.JcrConstants.MIX_VERSIONABLE;

public class AbstractDocumentWorkflowIntegrationTest extends RepositoryTestCase {

    protected Node handle;
    protected Node document;
    protected static final Credentials AUTHOR = new SimpleCredentials("author", "author".toCharArray());
    protected static final Credentials REVIEWER = new SimpleCredentials("reviewer", "reviewer".toCharArray());
    private Session authorSession;
    private Session reviewerSession;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final Node test = session.getRootNode().addNode("test", NT_FOLDER);
        createDocument(test);
        //test logins
        authorSession = server.login(AUTHOR);
        reviewerSession = server.login(REVIEWER);

        session.save();
    }

    private void createDocument(final Node test) throws RepositoryException {
        handle = test.addNode("document", NT_HANDLE);
        document = handle.addNode("document", NT_DOCUMENT);
        document.addMixin(HIPPOSTDPUBWF_DOCUMENT);
        document.addMixin(MIX_VERSIONABLE);
        document.addMixin(NT_RELAXED);
        document.setProperty(HIPPOSTDPUBWF_CREATION_DATE, Calendar.getInstance());
        document.setProperty(HIPPOSTDPUBWF_CREATED_BY, "testuser");
        document.setProperty(HIPPOSTDPUBWF_LAST_MODIFIED_DATE, Calendar.getInstance());
        document.setProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY, "testuser");
        document.setProperty(HIPPOSTD_STATE, UNPUBLISHED);
    }

    protected DocumentWorkflow getDocumentWorkflow(final Node handle) throws RepositoryException {
        WorkflowManager workflowManager = ((HippoWorkspace) session.getWorkspace()).getWorkflowManager();
        return (DocumentWorkflow) workflowManager.getWorkflow("default", handle);
    }

    protected DocumentWorkflow getDocumentWorkflowAsAuthor(final Node handle) throws RepositoryException {
        WorkflowManager workflowManager = ((HippoWorkspace) session.getWorkspace()).getWorkflowManager();
        return (DocumentWorkflow) workflowManager.getWorkflow("default", handle);
    }

    protected ReviewWorkflow getDocumentReviewWorkflowAsAuthor(final Node handle) throws RepositoryException {
        WorkflowManager workflowManager = ((HippoWorkspace) authorSession.getWorkspace()).getWorkflowManager();
        return (ReviewWorkflow) workflowManager.getWorkflow("default", handle);
    }

    protected ReviewWorkflow getDocumentReviewWorkflowAsReviewer(final Node handle) throws RepositoryException {
        WorkflowManager workflowManager = ((HippoWorkspace) reviewerSession.getWorkspace()).getWorkflowManager();
        return (ReviewWorkflow) workflowManager.getWorkflow("default", handle);
    }

    protected Node getVariant(final String state) throws RepositoryException {
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            if (state.equals(JcrUtils.getStringProperty(variant, HIPPOSTD_STATE, null))) {
                return variant;
            }
        }
        return null;
    }

    protected boolean isLive() throws RepositoryException {
        Node published = getVariant(PUBLISHED);
        if (published == null) {
            return false;
        }
        if (!published.hasProperty(HIPPO_AVAILABILITY)) {
            return false;
        }
        final Value[] availability = published.getProperty(HIPPO_AVAILABILITY).getValues();
        return toStringSet(availability).contains("live");
    }

    private Set<String> toStringSet(Value[] values) throws RepositoryException {
        Set<String> strings = new HashSet<>();
        for (Value value : values) {
            strings.add(value.getString());
        }
        return strings;
    }

    protected void poll(Executable executable, int seconds) throws Exception {
        while (true) {
            try {
                executable.execute();
                return;
            } catch (AssertionError e) {
                if (seconds-- <= 0) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
    }

    protected static interface Executable {
        void execute() throws Exception;
    }

}
