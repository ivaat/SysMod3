package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class MyCompoundTask extends MyTask {
    private MyBPMNModel nestedModel;

    private MyCompoundTask(String id, String label) {
        super(id, label);
    }

    public static MyCompoundTask create(String id, String label) {
        return new MyCompoundTask(id, label);
    }

    public MyBPMNModel getNestedModel() {
        return nestedModel;
    }

    public void setNestedModel(MyBPMNModel nestedModel) {
        this.nestedModel = nestedModel;
    }
}
