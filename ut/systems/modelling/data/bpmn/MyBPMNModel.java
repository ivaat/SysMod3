package ut.systems.modelling.data.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by taavi on 11/3/2016.
 */
public class MyBPMNModel {
    private final List<MyNode> nodes = new ArrayList<MyNode>();
    private MyCompoundTask parentTask;
    private MyEvent startEvent;
    private MyEvent endEvent;

    private MyBPMNModel() {}

    public static MyBPMNModel create() {
        return new MyBPMNModel();
    }

    public MyCompoundTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(MyCompoundTask parentTask) {
        this.parentTask = parentTask;
    }

    public void addNode(MyNode node) {
        this.nodes.add(node);
        node.setModel(this);
    }

    public MyEvent getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(MyEvent startEvent) {
        this.startEvent = startEvent;
    }

    public MyEvent getEndEvent() {
        return endEvent;
    }

    public void setEndEvent(MyEvent endEvent) {
        this.endEvent = endEvent;
    }

    public MyNode getNode(String id) {
        for (MyNode node : this.nodes) {
            if (Objects.equals(node.getId(), id)) {
                return node;
            }
        }

        return null; //TODO consider this
    }
}