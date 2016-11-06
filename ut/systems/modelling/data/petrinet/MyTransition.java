package ut.systems.modelling.data.petrinet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taavii on 5.11.2016.
 */
public class MyTransition {
    private final String id;
    private final String label;
    private final boolean isInvisible;
    private final List<MyPTArc> incomingArcs = new ArrayList<>();
    private final List<MyTPArc> outgoingArcs = new ArrayList<>();

    private MyTransition(String id, String label, boolean isInvisible) {
        this.id = id;
        this.label = label;
        this.isInvisible = isInvisible;
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

    //TODO - remove invisible parameter, doesn't seem to be needed
    public static MyTransition create(MyPetriNetModel pnModel, String id, String label, boolean isInvisible) {
        MyTransition transition = new MyTransition(id, label, isInvisible);
        pnModel.getTransitions().add(transition);
        return transition;
    }

    public boolean isInvisible() {
        return isInvisible;
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
