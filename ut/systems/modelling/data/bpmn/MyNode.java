package ut.systems.modelling.data.bpmn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stepan on 03/11/2016.
 */
public abstract class MyNode {
    private final List<MySequenceFlow> outgoing = new ArrayList<>();
    private final List<MySequenceFlow> incoming = new ArrayList<>();
    private MyBPMNModel model;
    private final String id;
    private final String label;

    protected MyNode(String id, String label) {
        this.id = id;
        this.label = label == null ? "" : label;
    }

    public List<MySequenceFlow> getIncoming() {
        return incoming;
    }

    public void addIncoming(MySequenceFlow newIncoming) {
        this.incoming.add(newIncoming);
    }

    public List<MySequenceFlow> getOutgoing() {
        return outgoing;
    }

    public void addOutgoing(MySequenceFlow newOutgoing) {
        this.outgoing.add(newOutgoing);
    }

    public MyBPMNModel getModel() {
        return model;
    }

    public void setModel(MyBPMNModel model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
