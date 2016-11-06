package ut.systems.modelling.data.bpmn;

/**
 * Created by stepan on 03/11/2016.
 */
public class MyGateway extends MyNode {
    private GatewayType type;

    private MyGateway(String id, String label) {
        super(id, label);
    }

    public static MyGateway create(String id, String label) {
        return new MyGateway(id, label);
    }

    public GatewayType getType() {
        return type;
    }

    public void setType(GatewayType type) {
        this.type = type;
    }

    public enum GatewayType {
        ANDSPLIT, XORSPLIT, ANDJOIN, XORJOIN
    }
}
