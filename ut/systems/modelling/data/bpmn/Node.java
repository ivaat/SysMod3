package ut.systems.modelling.data.bpmn;

import java.util.LinkedList;

/**
 * Created by stepan on 03/11/2016.
 */
public abstract class Node {
    private LinkedList<SequenceFlow> outgoing;
    private LinkedList<SequenceFlow> incoming;
    private BPMNModel model;
    private String id;

    public Node() {
        this.outgoing = new LinkedList<SequenceFlow>();
        this.incoming = new LinkedList<SequenceFlow>();
    }

    public LinkedList<SequenceFlow> getIncoming() {
        return incoming;
    }

    public void addIncoming(SequenceFlow newIncoming) {
        this.incoming.add(newIncoming);
    }

    public LinkedList<SequenceFlow> getOutgoing() {
        return outgoing;
    }

    public void addOutgoing(SequenceFlow newOutgoing) {
        this.outgoing.add(newOutgoing);
    }

    public BPMNModel getModel() {
        return model;
    }

    public void setModel(BPMNModel model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
