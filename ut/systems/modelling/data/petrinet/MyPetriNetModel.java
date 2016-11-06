package ut.systems.modelling.data.petrinet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taavi on 11/3/2016.
 */
public class MyPetriNetModel {
    private MyPlace startPlace;
    private MyPlace endPlace;
    private final List<MyPlace> allPlaces = new ArrayList<>();
    private final List<MyTransition> transitions = new ArrayList<>();

    private MyPetriNetModel() {};

    public static MyPetriNetModel create() {
        return new MyPetriNetModel();
    }

    public MyPlace getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(MyPlace startPlace) {
        this.startPlace = startPlace;
    }

    public MyPlace getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(MyPlace endPlace) {
        this.endPlace = endPlace;
    }

    public List<MyPlace> getAllPlaces() {
        return allPlaces;
    }

    public List<MyTransition> getTransitions() {
        return transitions;
    }

    public MyTransition getTransitionById(String id) {
        for (MyTransition transition : transitions) {
            if (transition.getId().equals(id)) {
                return transition;
            }
        }

        return null;
    }

    public MyPlace getPlaceById(String id) {
        for (MyPlace place : allPlaces) {
            if (place.getId().equals(id)) {
                return place;
            }
        }

        return null;
    }
}