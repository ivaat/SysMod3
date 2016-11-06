package ut.systems.modelling.data.petrinet;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Created by taavii on 6.11.2016.
 */
public class MyWrappedPetrinet {
    private final Petrinet petrinet;
    private final Marking initialMarking;

    public MyWrappedPetrinet(Petrinet petrinet, Marking initialMarking) {
        this.petrinet = petrinet;
        this.initialMarking = initialMarking;
    }

    public Petrinet getPetrinet() {
        return petrinet;
    }

    public Marking getInitialMarking() {
        return initialMarking;
    }
}
