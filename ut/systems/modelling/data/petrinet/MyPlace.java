package ut.systems.modelling.data.petrinet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taavii on 5.11.2016.
 */
public class MyPlace {
    private final String id;
    private String label;
    private final List<MyTPArc> incomingArcs = new ArrayList<>();
    private final List<MyPTArc> outgoingArcs = new ArrayList<>();
    private final MyPetriNetModel parent;

    private MyPlace(String id, String label, MyPetriNetModel parent) {
        this.id = id;
        this.label = label == null ? "" : label;
        this.parent = parent;
    };

    public static MyPlace createAndBind(MyPetriNetModel parent, String id, String label, MyTransition transition) {
        MyPlace place = new MyPlace(id, label, parent);
        parent.getAllPlaces().add(place);
        MyTPArc.createAndBind(transition, place);
        return place;
    }

    public static MyPlace createStartPlace(MyPetriNetModel parent, String id, String label) {
        MyPlace start = new MyPlace(id, label, parent);
        parent.setStartPlace(start);
        parent.getAllPlaces().add(start);
        return start;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public List<MyTPArc> getIncomingArcs() {
        return incomingArcs;
    }

    public List<MyPTArc> getOutgoingArcs() {
        return outgoingArcs;
    }

    public MyPetriNetModel getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
