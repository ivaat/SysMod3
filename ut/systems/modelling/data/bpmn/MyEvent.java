package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class MyEvent extends MyNode {
    private MyEvent(String id, String label) {
        super(id, label);
    }

    public static MyEvent create(String id, String label) {
        return new MyEvent(id, label);
    }
}
