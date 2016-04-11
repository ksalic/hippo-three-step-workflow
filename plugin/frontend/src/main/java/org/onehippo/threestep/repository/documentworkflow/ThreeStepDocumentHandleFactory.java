package org.onehippo.threestep.repository.documentworkflow;

import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.DocumentHandleFactory;

import javax.jcr.Node;

/**
 * @version "\$Id$" kenan
 */
public class ThreeStepDocumentHandleFactory implements DocumentHandleFactory {

    @Override
    public DocumentHandle createDocumentHandle(Node node) throws WorkflowException {
        return new ThreeStepDocumentHandle(node);
    }
}
