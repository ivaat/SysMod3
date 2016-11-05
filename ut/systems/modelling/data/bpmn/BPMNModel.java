package ut.systems.modelling.data.bpmn;

import java.util.LinkedList;

/**
 * Created by taavi on 11/3/2016.
 */
public class BPMNModel {
    private LinkedList<Node> nodes;
    private CompoundTask parentTask;
    private Event startEvent;
    private Event endEvent;

    public BPMNModel() {
        this.nodes = new LinkedList<Node>();
    }

    public CompoundTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(CompoundTask parentTask) {
        this.parentTask = parentTask;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
        node.setModel(this);
    }

    public Event getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Event startEvent) {
        this.startEvent = startEvent;
    }

    public Event getEndEvent() {
        return endEvent;
    }

    public void setEndEvent(Event endEvent) {
        this.endEvent = endEvent;
    }

    public Node getNode(String id) {
        for (Node node : this.nodes) {
            if (node.getId() == id) {
                return node;
            }
        }

        return null;
    }
}
