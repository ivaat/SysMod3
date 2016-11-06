package ut.systems.modelling.data.petrinet;

/**
 * Created by taavii on 5.11.2016.
 */
public class MyPTArc implements MyIArc<MyPlace, MyTransition> {
    private final MyPlace source;
    private final MyTransition target;

    private MyPTArc(MyPlace source, MyTransition target) {
        this.source = source;
        this.target = target;
    };

    @Override
    public MyPlace getSource() {
        return this.source;
    }

    @Override
    public MyTransition getTarget() {
        return this.target;
    }

    public static MyPTArc createAndBind(MyPlace source, MyTransition target) {
        MyPTArc arc = new MyPTArc(source, target);
        source.getOutgoingArcs().add(arc);
        target.getIncomingArcs().add(arc);
        return arc;
    }

    @Override
    public String toString() {
        return source.toString() + " -> " + target.toString();
    }
}