package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class MySequenceFlow {
    private MyNode source;
    private MyNode destination;

    private MySequenceFlow() {}

    public static MySequenceFlow create() {
        return new MySequenceFlow();
    }

    public MyNode getDestination() {
        return destination;
    }

    public void setDestination(MyNode destination) {
        this.destination = destination;
    }

    public MyNode getSource() {
        return source;
    }

    public void setSource(MyNode source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return source.toString() + " -> " + destination.toString();
    }
}
