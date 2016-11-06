package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class MySimpleTask extends MyTask {
    private MySimpleTask(String id, String label) {
        super(id, label);
    }

    public static MySimpleTask create(String id, String label) {
        return new MySimpleTask(id, label);
    }
}
