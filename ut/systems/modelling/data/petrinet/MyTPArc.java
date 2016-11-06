package ut.systems.modelling.data.petrinet;

/**
 * Created by taavii on 5.11.2016.
 */
public class MyTPArc implements MyIArc<MyTransition, MyPlace> {
    private final MyTransition source;
    private final MyPlace target;

    private MyTPArc(MyTransition source, MyPlace target) {
        this.source = source;
        this.target = target;
    };

    @Override
    public MyTransition getSource() {
        return this.source;
    }

    @Override
    public MyPlace getTarget() {
        return this.target;
    }

    public static MyTPArc createAndBind(MyTransition source, MyPlace target) {
        MyTPArc arc = new MyTPArc(source, target);
        source.getOutgoingArcs().add(arc);
        target.getIncomingArcs().add(arc);
        return arc;
    }

    @Override
    public String toString() {
        return source.toString() + " -> " + target.toString();
    }
}
