package ut.systems.modelling.data.petrinet;

/**
 * Created by taavii on 5.11.2016.
 */
public interface MyIArc<SourceType, TargetType> {
    SourceType getSource();
    TargetType getTarget();
}
