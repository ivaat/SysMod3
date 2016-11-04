package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public abstract class Task extends Node {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
