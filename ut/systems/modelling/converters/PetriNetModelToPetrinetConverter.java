package ut.systems.modelling.converters;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import ut.systems.modelling.data.petrinet.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by taavii on 5.11.2016.
 */
public class PetriNetModelToPetrinetConverter implements IConverter<MyPetriNetModel, MyWrappedPetrinet> {
    @Override
    public MyWrappedPetrinet convert(MyPetriNetModel petriNetModel) {
        Petrinet petrinet = new PetrinetImpl(petriNetModel.getLabel());
        Map<MyPlace, Place> placeMapping = new HashMap<>();
        Map<MyTransition, Transition> transitionMapping = new HashMap<>();

        for (MyTransition myTransition : petriNetModel.getTransitions()) {
            transitionMapping.put(myTransition, petrinet.addTransition(myTransition.getName()));
        }

        for (MyPlace myPlace : petriNetModel.getAllPlaces()) {
            placeMapping.put(myPlace, petrinet.addPlace(myPlace.getLabel()));
        }

        for (MyTransition myTransition : petriNetModel.getTransitions()) {
            for (MyTPArc outgoing : myTransition.getOutgoingArcs()) {
                petrinet.addArc(transitionMapping.get(myTransition), placeMapping.get(outgoing.getTarget()));
            }
        }

        for (MyPlace myPlace : petriNetModel.getAllPlaces()) {
            for (MyPTArc outgoing : myPlace.getOutgoingArcs()) {
                petrinet.addArc(placeMapping.get(myPlace), transitionMapping.get(outgoing.getTarget()));
            }
        }

        Marking initialMarking = new Marking();
        initialMarking.add(placeMapping.get(petriNetModel.getStartPlace()));

        return new MyWrappedPetrinet(petrinet, initialMarking);
    }
}
