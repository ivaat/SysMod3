package ut.systems.modelling.converters;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import ut.systems.modelling.data.bpmn.*;

import java.util.Collection;

/**
 * Created by taavii on 5.11.2016.
 */
public class BpmnDiagramToBpmnModelConverter implements IConverter<BPMNDiagram, MyBPMNModel>{
    @Override
    public MyBPMNModel convert(BPMNDiagram diagram) {
        MyBPMNModel ourBPMN = MyBPMNModel.create();

        Collection<Event> events = diagram.getEvents();

        for (Event event : events) {
            if (event.getEventType() == Event.EventType.START) {
                processProMNode(event, diagram, ourBPMN);
            }
        }

        return ourBPMN;
    }

    private static MyNode processProMNode(BPMNNode node, BPMNDiagram diagram, MyBPMNModel model) {
        MyNode existingNode = model.getNode(node.getId().toString());

        if (existingNode != null) {
            return existingNode; // for join gateways etc nodes that have multiple incoming flows
        }

        MyNode newNode = null;

        if (node instanceof Event) {
            newNode = MyEvent.create(node.getId().toString(), node.getLabel());
            model.addNode(newNode);

            if (((Event) node).getEventType() == Event.EventType.START) {
                model.setStartEvent((MyEvent) newNode);
            } else if (((Event) node).getEventType() == Event.EventType.END) {
                model.setEndEvent((MyEvent) newNode);
            }
        } else if (node instanceof Activity) {
            if (node instanceof SubProcess) {
                newNode = MyCompoundTask.create(node.getId().toString(), node.getLabel());

                // TODO: if we have the time. It's been said that a plugin that only converts a flat one is better than a plugin that doesn't convert anything : )
            } else {
                newNode = MySimpleTask.create(node.getId().toString(), node.getLabel());

                model.addNode(newNode);
            }
        } else if (node instanceof Gateway) {
            newNode = MyGateway.create(node.getId().toString(), node.getLabel());

            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> inEdges = diagram.getInEdges(node);
            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> outEdges = diagram.getOutEdges(node);

            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.PARALLEL) { // TODO: Not sure if this is the correct way to check
                if (inEdges.size() > outEdges.size()) {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.ANDJOIN);
                } else {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.ANDSPLIT);
                }
            } else { // TODO: should be more specific? //TODO check that its Databased, throw an exception otherwise
                if (inEdges.size() > outEdges.size()) {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.XORJOIN);
                } else {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.XORSPLIT);
                }
            }

            model.addNode(newNode);
        }

        for (BPMNEdge edge : diagram.getOutEdges(node)) {
            BPMNNode nextNode = (BPMNNode)edge.getTarget();

            MySequenceFlow ourFlow = MySequenceFlow.create();
            ourFlow.setSource(newNode);

            newNode.addOutgoing(ourFlow);

            MyNode nextOurNode = processProMNode(nextNode, diagram, model);

            ourFlow.setDestination(nextOurNode);

            nextOurNode.addIncoming(ourFlow);
        }

        return newNode;
    }
}
