package org.onehippo.threestep.cms.workflow.plugins.reviewedactions;

import org.apache.wicket.model.Model;
import org.hippoecm.frontend.i18n.model.NodeTranslator;
import org.hippoecm.frontend.plugins.reviewedactions.AbstractDocumentWorkflowPlugin;
import org.hippoecm.frontend.plugins.reviewedactions.UnpublishedReferenceNodeProvider;
import org.onehippo.threestep.cms.workflow.ReviewWorkflow;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.TextDialog;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.reviewedactions.dialogs.SchedulePublishDialog;
import org.hippoecm.frontend.plugins.reviewedactions.dialogs.UnpublishedReferencesDialog;
import org.hippoecm.frontend.plugins.reviewedactions.model.ReferenceProvider;
import org.hippoecm.frontend.plugins.reviewedactions.model.Request;
import org.hippoecm.frontend.plugins.reviewedactions.model.RequestModel;
import org.hippoecm.frontend.plugins.reviewedactions.model.UnpublishedReferenceProvider;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is the workflow menu plugin for the threestep workflow action for the reviewer role
 * @version "\$Id$" kenan
 */
public class ReviewRequestsWorkflowPlugin extends AbstractDocumentWorkflowPlugin {

    static final Logger log = LoggerFactory.getLogger(ReviewRequestsWorkflowPlugin.class);

    public ReviewRequestsWorkflowPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        final Map<String, Serializable> info = getHints();

        final List<IModel<Request>> requests = new ArrayList<>();
        WorkflowDescriptorModel model = getModel();
        Workflow workflow = model.getWorkflow();
        if (workflow != null) {
            if (info.containsKey("requests")) {
                Map<String, Map<String, ?>> infoRequests = (Map<String, Map<String, ?>>) info.get("requests");
                for (Map.Entry<String, Map<String, ?>> entry : infoRequests.entrySet()) {
                    requests.add(new RequestModel(entry.getKey(), entry.getValue()));
                }
            }
        }

        final StdWorkflow requestReview;
        add(requestReview = new StdWorkflow("requestReview", new StringResourceModel("request-review", this, null), context, getModel()) {

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Node handle = ((WorkflowDescriptorModel) getDefaultModel()).getNode();
                    Node unpublished = getVariant(handle, HippoStdNodeType.UNPUBLISHED);
                    final UnpublishedReferenceProvider referenced = new UnpublishedReferenceProvider(new ReferenceProvider(
                            new JcrNodeModel(unpublished)));
                    if (referenced.size() > 0) {
                        return new UnpublishedReferencesDialog(this, new UnpublishedReferenceNodeProvider(referenced), getEditorManager());
                    }
                } catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
                return null;
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.requestReview();
                return null;
            }
        });

        final StdWorkflow requestPublicationReview;
        add(requestPublicationReview = new StdWorkflow("requestPublicationReview", new StringResourceModel("request-scheduled-review", this, null), context, getModel()) {
            public Date date = new Date();

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE_CLOCK);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                WorkflowDescriptorModel wdm = getModel();
                try {
                    Node unpublished = getVariant(wdm.getNode(), HippoStdNodeType.UNPUBLISHED);
                    final IModel<String> titleModel = new StringResourceModel("schedule-publish-title",
                            ReviewRequestsWorkflowPlugin.this, null, getDocumentName());
                    return new SchedulePublishDialog(this, new JcrNodeModel(unpublished),
                            PropertyModel.of(this, "date"), titleModel, getEditorManager());
                } catch (RepositoryException ex) {
                    log.warn("could not retrieve node for scheduling publish", ex);
                }
                return null;
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                if (date != null) {
                    workflow.requestPublicationReview(date);
                } else {
                    workflow.requestReview();
                }
                return null;
            }
        });

        final StdWorkflow acceptReview;
        add(acceptReview = new StdWorkflow("acceptReview", new StringResourceModel("accept-review", this, null), context, getModel()) {

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE);
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                final String idFromRequestKey = getIdFromRequestKey("acceptReview");
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.acceptReview(idFromRequestKey);
                return null;
            }
        });

        final StdWorkflow rejectReview;
        add(rejectReview = new StdWorkflow("rejectReview", new StringResourceModel("reject-review", this, null), context, getModel()) {

            public String reason;

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.MINUS_CIRCLE);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                final StringResourceModel title = new StringResourceModel("rejected-review-title", ReviewRequestsWorkflowPlugin.this, null);
                final StringResourceModel text = new StringResourceModel("rejected-review-text", ReviewRequestsWorkflowPlugin.this, null);
                final StdWorkflow stdWorkflow = this;
                return new TextDialog(
                        title,
                        text,
                        new PropertyModel<>(this, "reason")) {
                    @Override
                    public void invokeWorkflow() throws Exception {
                        stdWorkflow.invokeWorkflow();
                    }
                };
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                final String idFromRequestKey = getIdFromRequestKey("rejectReview");
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.rejectReview(idFromRequestKey, reason);
                return null;
            }
        });


        if (isActionAllowed(info, "requestReview") || isActionAllowed(info, "acceptReview")||isActionAllowed(info, "rejectReview")) {

            if (!info.containsKey("publish")||info.containsKey("requestReview")) {
                if (!info.containsKey("publish")) {
                    hideOrDisable(info, "requestReview", requestReview, requestPublicationReview);
                }
                if (info.containsKey("requestReview")) {
                    hideOrDisable(info, "acceptReview", acceptReview);
                    hideOrDisable(info, "rejectReview", rejectReview);
                }
            } else {
                requestReview.setVisible(false);
                requestPublicationReview.setVisible(false);
                acceptReview.setVisible(false);
                rejectReview.setVisible(false);
            }

        } else {
            requestReview.setVisible(false);
            requestPublicationReview.setVisible(false);
            acceptReview.setVisible(false);
            rejectReview.setVisible(false);
        }

    }

    IModel<String> getDocumentName() {
        try {
            return (new NodeTranslator(new JcrNodeModel(((WorkflowDescriptorModel) getDefaultModel()).getNode())))
                    .getNodeName();
        } catch (RepositoryException ex) {
            try {
                return Model.of(((WorkflowDescriptorModel) getDefaultModel()).getNode().getName());
            } catch (RepositoryException e) {
                return new StringResourceModel("unknown", this, null);
            }
        }
    }

    public String getIdFromRequestKey(String key) {
        final Map<String, Serializable> info = getHints();
        if (info.containsKey("requests")) {
            Map<String, Map<String, ?>> infoRequests = (Map<String, Map<String, ?>>) info.get("requests");
            for (Map.Entry<String, Map<String, ?>> entry : infoRequests.entrySet()) {
                for (Map.Entry<String, ?> type : entry.getValue().entrySet()) {
                    if (type.getKey().equals(key) && (Boolean) type.getValue()) {
                        return entry.getKey();
                    }
                }
            }
        }
        throw new IllegalArgumentException("No request found, please check your scxml definition for errors");
    }


}
