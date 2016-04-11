package org.onehippo.threestep.repository.documentworkflow;

import org.onehippo.repository.documentworkflow.*;
import org.onehippo.threestep.cms.workflow.ThreeStepNodeType;
import org.hippoecm.repository.HippoStdPubWfNodeType;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version "\$Id$" kenan
 */
public class ThreeStepDocumentHandle extends DocumentHandle {

    private static final Logger log = LoggerFactory.getLogger(DocumentHandle.class);

    private final Node handle;
    private Map<String, DocumentVariant> documents = new HashMap<>();
    private Map<String, Request> requests = new HashMap<>();
    private Boolean requestPending = false;
    private boolean initialized;

    public ThreeStepDocumentHandle(Node handle) throws WorkflowException {
        super(handle);
        this.handle = handle;
    }

    protected DocumentVariant createDocumentVariant(Node node) throws RepositoryException {
        return new DocumentVariant(node);
    }

    protected Request createRequest(Node node) throws RepositoryException {
        Request request = null;
        if (node.isNodeType(ThreeStepNodeType.THREESTEP_REQUEST)) {
            request = new ReviewRequest(node);
        } else {
            request = Request.createWorkflowRequest(node);
            if (request == null) {
                request = Request.createScheduledRequest(node);
            }

        }
        return request;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    @Override
    public void initialize() throws WorkflowException {
        if (initialized) {
            reset();
        }
        initialized = true;

        try {
            for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
                DocumentVariant doc = createDocumentVariant(variant);
                if (documents.containsKey(doc.getState())) {
                    log.warn("Document at path {} has multiple variants with state {}. Variant with identifier {} ignored.",
                            handle.getPath(), doc.getState(), variant.getIdentifier());
                }
                documents.put(doc.getState(), doc);
            }

            for (Node requestNode : new NodeIterable(handle.getNodes(HippoStdPubWfNodeType.HIPPO_REQUEST))) {
                Request request = createRequest(requestNode);
                if (request != null) {
                    if (request.isWorkflowRequest()) {
                        requests.put(request.getIdentity(), request);
                        if (!HippoStdPubWfNodeType.REJECTED.equals(((WorkflowRequest) request).getType())) {
                            requestPending = true;
                        }
                    } else if (request.isScheduledRequest()) {
                        requests.put(request.getIdentity(), request);
                        requestPending = true;
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new WorkflowException("DocumentHandle initialization failed", e);
        }
    }

    @Override
    public void reset() {
        if (initialized) {
            documents.clear();
            requests.clear();
            requestPending = false;
            initialized = false;
        }
    }

    public Node getHandle() {
        return handle;
    }

    public Map<String, Request> getRequests() {
        return requests;
    }

    public boolean isRequestPending() {
        return requestPending;
    }

    public Map<String, DocumentVariant> getDocuments() {
        return documents;
    }
}