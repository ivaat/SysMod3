package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class CompoundTask extends Task {
    private BPMNModel nestedModel;

    public CompoundTask(String name) {
        super(name);
    }

    public BPMNModel getNestedModel() {
        return nestedModel;
    }

    public void setNestedModel(BPMNModel nestedModel) {
        this.nestedModel = nestedModel;
    }
}
