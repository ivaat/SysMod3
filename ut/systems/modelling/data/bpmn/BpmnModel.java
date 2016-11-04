package ut.systems.modelling.data.bpmn;

import java.util.LinkedList;

/**
 * Created by taavi on 11/3/2016.
 */
public class BPMNModel {
    private LinkedList<Node> nodes;
    private CompoundTask parentTask;

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
}
