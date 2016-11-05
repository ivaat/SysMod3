package ut.systems.modelling;

import de.hpi.bpt.process.GatewayType;
import org.processmining.analysis.petrinet.cpnexport.ColoredTransition;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import ut.systems.modelling.data.bpmn.*;

import java.util.Collection;

@Plugin(
        name = "Converter BPMN-PN",
        parameterLabels = { "BPMNDiagram" },
        returnLabels = { "Petri-Net" },
        returnTypes = { Petrinet.class },
        userAccessible = true,
        help = "Convert a BPMN diagram into a Petri-Net"
)
public class ConverterPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Taavi Ilp & Stepan Bolotnikov",
            email = "taaviilp@ut.ee & stepan@ut.ee"
    )
    @PluginVariant(variantLabel = "Convert BPMN into PN", requiredParameterLabels = {0})
    public static Petrinet optimizeDiagram(UIPluginContext context, BPMNDiagram diagram) {
        Petrinet pn = null;

//        MyBPMNModel myBPMNModel = getMyBPMNModel(diagram);
//        pn = MyConverter.getPN(myBPMNModel);

        BPMNModel ourBPMN = ConverterPlugin.getOurBPMN(diagram);

        return pn;
    }

    private static BPMNModel getOurBPMN(BPMNDiagram diagram) {
        BPMNModel ourBPMN = new BPMNModel();

        Collection<org.processmining.models.graphbased.directed.bpmn.elements.Event> events = diagram.getEvents();

        for (org.processmining.models.graphbased.directed.bpmn.elements.Event event : events) {
            if (event.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
                ConverterPlugin.processProMNode(event, diagram, ourBPMN);
            }
        }

        return ourBPMN;
    }

    private static Node processProMNode(BPMNNode node, BPMNDiagram diagram, BPMNModel model) {
        Node existingNode = model.getNode(node.getId().toString());

        if (existingNode != null) {
            return existingNode; // for join gateways etc nodes that have multiple incoming flows
        }

        Node newNode = null;

        if (node instanceof org.processmining.models.graphbased.directed.bpmn.elements.Event) {
            newNode = new ut.systems.modelling.data.bpmn.Event();

            model.addNode(newNode);

            if (((org.processmining.models.graphbased.directed.bpmn.elements.Event) node).getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
                model.setStartEvent((ut.systems.modelling.data.bpmn.Event) newNode);
            } else if (((org.processmining.models.graphbased.directed.bpmn.elements.Event) node).getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END) {
                model.setEndEvent((ut.systems.modelling.data.bpmn.Event) newNode);
            }
        } else if (node instanceof Activity) {
            if (node instanceof SubProcess) {
                newNode = new CompoundTask(node.getLabel());


                // TODO: if we have the time. It's been said that a plugin that only converts a flat one is better than a plugin that doesn't convert anything : )
            } else {
                newNode = new SimpleTask(node.getLabel());

                model.addNode(newNode);
            }
        } else if (node instanceof org.processmining.models.graphbased.directed.bpmn.elements.Gateway) {
            newNode = new ut.systems.modelling.data.bpmn.Gateway();

            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> inEdges = diagram.getInEdges(node);
            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> outEdges = diagram.getOutEdges(node);

            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.INCLUSIVE) { // TODO: Not sure if this is the correct way to check
                if (inEdges.size() > outEdges.size()) {
                    ((ut.systems.modelling.data.bpmn.Gateway) newNode).setType(ut.systems.modelling.data.bpmn.Gateway.gatewayType.ANDJOIN);
                } else {
                    ((ut.systems.modelling.data.bpmn.Gateway) newNode).setType(ut.systems.modelling.data.bpmn.Gateway.gatewayType.ANDSPLIT);
                }
            } else { // TODO: should be more specific?
                if (inEdges.size() > outEdges.size()) {
                    ((ut.systems.modelling.data.bpmn.Gateway) newNode).setType(ut.systems.modelling.data.bpmn.Gateway.gatewayType.XORJOIN);
                } else {
                    ((ut.systems.modelling.data.bpmn.Gateway) newNode).setType(ut.systems.modelling.data.bpmn.Gateway.gatewayType.XORSPLIT);
                }
            }
        }

        newNode.setId(node.getId().toString());

        for (BPMNEdge edge : diagram.getOutEdges(node)) {
            BPMNNode nextNode = (BPMNNode)edge.getTarget();

            SequenceFlow ourFlow = new SequenceFlow();
            ourFlow.setSource(newNode);

            newNode.addOutgoing(ourFlow);

            Node nextOurNode = ConverterPlugin.processProMNode(nextNode, diagram, model);

            ourFlow.setDestination(nextOurNode);

            nextOurNode.addIncoming(ourFlow);
        }

        return newNode;
    }
}
