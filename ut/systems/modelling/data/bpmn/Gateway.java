package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class Gateway extends Node {
    private type type;
    public enum type {
        ANDSPLIT, XORSPLIT, ANDJOIN, XORJOIN
    }

    public Gateway.type getType() {
        return type;
    }

    public void setType(Gateway.type type) {
        this.type = type;
    }
}
