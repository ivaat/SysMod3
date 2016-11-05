package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class Gateway extends Node {
    private gatewayType type;
    public enum gatewayType {
        ANDSPLIT, XORSPLIT, ANDJOIN, XORJOIN
    }

    public Gateway.gatewayType getType() {
        return type;
    }

    public void setType(Gateway.gatewayType type) {
        this.type = type;
    }
}
