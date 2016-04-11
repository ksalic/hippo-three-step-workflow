package org.onehippo.repository.documentworkflow;

import org.hippoecm.repository.HippoStdPubWfNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.util.JcrConstants;
import org.onehippo.threestep.cms.workflow.ThreeStepNodeType;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Date;

/**
 * @version "\$Id$" kenan
 */
public class ReviewRequest extends WorkflowRequest {

    public ReviewRequest(Node node) throws RepositoryException {
        super(node);
    }

    public static Node newRequestNode(Node parent) throws RepositoryException {
        JcrUtils.ensureIsCheckedOut(parent);
        Node requestNode = parent.addNode(HippoStdPubWfNodeType.HIPPO_REQUEST, ThreeStepNodeType.THREESTEP_REQUEST);
        requestNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
        return requestNode;
    }

    public ReviewRequest(String type, String threeStepType, Node sibling, DocumentVariant document, String username) throws RepositoryException {
        super(newRequestNode(sibling.getParent()));
        setStringProperty(HippoStdPubWfNodeType.HIPPOSTDPUBWF_TYPE, type);
        setStringProperty(ThreeStepNodeType.THREESTEP_TYPE, threeStepType);
        setStringProperty(HippoStdPubWfNodeType.HIPPOSTDPUBWF_USERNAME, username);
        if (document != null) {
            getCheckedOutNode().setProperty(HippoStdPubWfNodeType.HIPPOSTDPUBWF_DOCUMENT, document.getNode());
        }
    }

    public String getThreeStepType() throws RepositoryException {
        return getStringProperty(ThreeStepNodeType.THREESTEP_TYPE);
    }

    public ReviewRequest(String type, String threeStepType, Node sibling, DocumentVariant document, String username, Date scheduledDate) throws RepositoryException {
        this(type, threeStepType, sibling, document, username);
        setDateProperty(HippoStdPubWfNodeType.HIPPOSTDPUBWF_REQDATE, scheduledDate);
    }

}