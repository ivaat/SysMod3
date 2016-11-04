package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class SequenceFlow {
    private Node source;
    private Node destination;

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }
}
