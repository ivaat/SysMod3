package ut.systems.modelling.data.petrinet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taavii on 5.11.2016.
 */
public class MyTransition {
    private final String id;
    private final String label;
    private final List<MyPTArc> incomingArcs = new ArrayList<>();
    private final List<MyTPArc> outgoingArcs = new ArrayList<>();

    private MyTransition(String id, String label) {
        this.id = id;
        this.label = label;
    };

    public String getName() {
        return this.label;
    }

    public List<MyPTArc> getIncomingArcs() {
        return this.incomingArcs;
    }

    public List<MyTPArc> getOutgoingArcs() {
        return this.outgoingArcs;
    }

    public static MyTransition createAndBind(MyPetriNetModel pnModel, String id, String label, MyPlace place) {
        MyTransition transition = new MyTransition(id, label);
        pnModel.getTransitions().add(transition);
        MyPTArc.createAndBind(place, transition);
        return transition;
    }

    public String getId() {
        return id;
    }

    public MyTPArc getOutgoingArcByPlace(MyPlace place) {
        for (MyTPArc arc : outgoingArcs) {
            if (arc.getTarget().equals(place)) {
                return arc;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
