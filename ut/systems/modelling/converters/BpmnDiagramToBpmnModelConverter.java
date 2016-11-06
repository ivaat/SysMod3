package ut.systems.modelling.converters;

import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
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
    public MyBPMNModel convert(BPMNDiagram diagram) throws Exception {
        MyBPMNModel ourBPMN = MyBPMNModel.create(diagram.getLabel());

        Collection<Event> events = diagram.getEvents();

        for (Event event : events) {
            if (event.getParentSubProcess() == null && event.getEventType() == Event.EventType.START) {
                processProMNode(event, diagram, ourBPMN);
            }
        }

        return ourBPMN;
    }

    private static MyNode processProMNode(BPMNNode node, BPMNDiagram diagram, MyBPMNModel model) throws Exception {
        MyNode existingNode = model.getNodeById(node.getId().toString());

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
                //we're currently considering only the case where the subprocess node has just one child and it is a BPMN diagram (can be nested)
                SubProcess subProcess = (SubProcess)node;
                MyBPMNModel subModel = MyBPMNModel.create(subProcess.getLabel());

                for (ContainableDirectedGraphElement element : subProcess.getChildren()) {
                    if (    element instanceof Event &&
                            ((Event) element).getEventType() == Event.EventType.START &&
                            ((Event) element).getParentSubProcess() == subProcess) {
                        processProMNode((Event)element, diagram, subModel);
                        break;
                    }
                }

                subModel.setParentTask((MyCompoundTask) newNode);
                ((MyCompoundTask) newNode).setNestedModel(subModel);
                model.addNode(newNode);
            } else {
                newNode = MySimpleTask.create(node.getId().toString(), node.getLabel());
                model.addNode(newNode);
            }
        } else if (node instanceof Gateway) {
            newNode = MyGateway.create(node.getId().toString(), node.getLabel());

            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> inEdges = diagram.getInEdges(node);
            Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> outEdges = diagram.getOutEdges(node);

            //strictly speaking the below tests aren't 100% accu
            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.PARALLEL) {
                if (outEdges.size() == 1) {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.ANDJOIN);
                }
                else if (inEdges.size() == 1){
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.ANDSPLIT);
                }
                else {
                    throw new Exception("Incorrect AND gateway encountered. Incoming edges: " + inEdges.size() + ", outgoing edges: " + outEdges.size());
                }
            }
            else if (((Gateway) node).getGatewayType() == Gateway.GatewayType.DATABASED) {
                if (outEdges.size() == 1) {
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.XORJOIN);
                }
                else if (inEdges.size() == 1){
                    ((MyGateway) newNode).setType(MyGateway.GatewayType.XORSPLIT);
                }
                else {
                    throw new Exception("Incorrect AND gateway encountered. Incoming edges: " + inEdges.size() + ", outgoing edges: " + outEdges.size());
                }
            }
            else {
                throw new Exception("Unknown gateway type encountered:" + ((Gateway) node).getGatewayType());
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
