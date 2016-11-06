package ut.systems.modelling.converters;

import org.processmining.framework.plugin.InSufficientResultException;
import ut.systems.modelling.data.bpmn.*;
import ut.systems.modelling.data.petrinet.*;

/**
 * Created by taavii on 5.11.2016.
 */
public class BpmnModelToPetriNetModelConverter implements IConverter<MyBPMNModel, MyPetriNetModel> {
    private static int placeLabelSequence = 1;
    private static int transitionLabelSequence = 1;

    @Override
    public MyPetriNetModel convert(MyBPMNModel bpmnModel) throws Exception {
        MyPetriNetModel pnModel = MyPetriNetModel.create();

        //start event > start place + transition
        MyPlace startPlace = MyPlace.createStartPlace(pnModel, bpmnModel.getStartEvent().getId(), bpmnModel.getStartEvent().getLabel());

        //start event must have only one outgoing flow
        convertSequenceFlow(bpmnModel.getStartEvent().getOutgoing().get(0), null, startPlace, pnModel);

        //end event > end place + transition

        //each task > transition
        //each gateway > transition

        //between two nodes - place
        //so basically node-sequenceflow-node gets translated to
        // place-arc-transition-arc-place

        //event-sf-task-sf-event
        //place-arc-trans-arc-place-arc-trans-arc-place

        //startnode - sequenceflow - endnode

        //startplace - arc

        //FIXME
        return pnModel;
    }

    private void convertSequenceFlow(MySequenceFlow flow, MyTransition prevTransition, MyPlace parentPlace, MyPetriNetModel pnModel) throws Exception {
        MyNode dest = flow.getDestination();
        //can only be end event
        if (dest instanceof MyEvent) {
            //we already have the end place prepared - it's the incoming parent place
            pnModel.setEndPlace(parentPlace);
            parentPlace.setLabel(dest.getLabel());
        }
        else if (dest instanceof MyGateway) {
            //TODO - test cases where you have gates in direct sequence
            //if gateway has 1 incoming and 1 outgoing flow, we can basically remove this gateway
            if (dest.getOutgoing().size() == 1 && dest.getIncoming().size() == 1) {
                //this gateway is completely meaningless, let's skip it and process the subsequent flow
                convertSequenceFlow(dest.getOutgoing().get(0), prevTransition, parentPlace, pnModel);
            }
            else {
                MyGateway gateway = (MyGateway)dest;
                if (gateway.getType() == MyGateway.GatewayType.ANDSPLIT) {
                    //if previous non-place was startevent
                    //convert to transition with several outgoings and handle it as such
                    if (pnModel.getStartPlace() == parentPlace) {
                        MyTransition transition = MyTransition.create(pnModel, dest.getId(), dest.getLabel(), false);
                        MyPTArc.createAndBind(parentPlace, transition);
                        for (MySequenceFlow outgoing : gateway.getOutgoing()) {
                            //create arc & place first
                            MyPlace place = MyPlace.create(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++);
                            MyTPArc.createAndBind(transition, place);
                            convertSequenceFlow(outgoing, transition, place, pnModel);
                        }
                    }
                    else {
                        //if previous non-place was transition
                        //transform the last transition into this gateway
                        //for one sequence flow you already have made arc & place - current one
                        //so for first one just use existing one
                        //for all other flows create new transition & places and handle the sequence flows

                        boolean first = true;
                        for (MySequenceFlow outgoing: gateway.getOutgoing()) {
                            if (first) {
                                convertSequenceFlow(outgoing, prevTransition, parentPlace, pnModel);
                                first = false;
                            }
                            else {
                                //create arc & place first
                                MyPlace place = MyPlace.create(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++);
                                MyTPArc.createAndBind(prevTransition, place);
                                convertSequenceFlow(outgoing, prevTransition, place, pnModel);
                            }
                        }
                    }
                }
                else if (gateway.getType() == MyGateway.GatewayType.ANDJOIN) {
                    //convert to transition, tie all incoming arcs to this
                    MyTransition transition = pnModel.getTransitionById(gateway.getId());
                    //TODO - add createandbind to transition create method
                    if (transition == null) {
                        transition = MyTransition.create(pnModel, gateway.getId(), gateway.getLabel(), false);
                        MyPlace place = MyPlace.create(pnModel, gateway.getId(), "p" + placeLabelSequence++);
                        MyTPArc.createAndBind(transition, place);
                        convertSequenceFlow(gateway.getOutgoing().get(0), transition, place, pnModel);
                    }
                    MyPTArc.createAndBind(parentPlace, transition);
                }
                else if (gateway.getType() == MyGateway.GatewayType.XORSPLIT) {
                    //from parentplace to each subsequent sequenceflow
                    if (parentPlace != pnModel.getStartPlace()) {
                        parentPlace.setLabel(gateway.getLabel());
                    }
                    for (MySequenceFlow outgoing : gateway.getOutgoing()) {
                        //TODO - do the same for AND, check against test7 then
                        if (outgoing.getDestination() instanceof MyGateway) {
                            //create dummy transition & place
                            MyTransition transition = MyTransition.create(pnModel, "t" + transitionLabelSequence, "t" + transitionLabelSequence++, false);
                            MyPTArc.createAndBind(parentPlace, transition);
                            MyPlace dummyPlace = MyPlace.create(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++);
                            MyTPArc.createAndBind(transition, dummyPlace);
                            convertSequenceFlow(outgoing, transition, dummyPlace, pnModel);
                        }
                        else {
                            convertSequenceFlow(outgoing, prevTransition, parentPlace, pnModel);
                        }
                    }
                }
                else if (gateway.getType() == MyGateway.GatewayType.XORJOIN) {
                    //TODO - do similar for ANDJOIN!
                    if (flow.getSource() instanceof MyGateway) {
                        //two gateways in a row - we need the dummy place & + new transition
                        MyTransition dummyTransition = MyTransition.create(pnModel, "t" + transitionLabelSequence, "t" + transitionLabelSequence++, false);
                        MyPTArc.createAndBind(parentPlace, dummyTransition);

                        MyPlace xorPlace = pnModel.getPlaceById(gateway.getId());
                        if (xorPlace == null) {
                            xorPlace = MyPlace.create(pnModel, gateway.getId(), gateway.getLabel());
                            MyTPArc.createAndBind(dummyTransition, xorPlace);
                            convertSequenceFlow(gateway.getOutgoing().get(0), dummyTransition, xorPlace, pnModel);
                        } else {
                            MyTPArc.createAndBind(dummyTransition, xorPlace);
                        }
                    }
                    else {
                        //we created the place prematurely and must now backtrack
                        //meaning that we must delete the created place as well as delete the arc between prevTransition and parentPlace
                        pnModel.getAllPlaces().remove(parentPlace);
                        prevTransition.getOutgoingArcs().remove(prevTransition.getOutgoingArcByPlace(parentPlace));

                        MyPlace xorPlace = pnModel.getPlaceById(gateway.getId());
                        if (xorPlace == null) {
                            xorPlace = MyPlace.create(pnModel, gateway.getId(), gateway.getLabel());
                            MyTPArc.createAndBind(prevTransition, xorPlace);
                            convertSequenceFlow(gateway.getOutgoing().get(0), prevTransition, xorPlace, pnModel);
                        } else {
                            MyTPArc.createAndBind(prevTransition, xorPlace);
                        }
                    }
                }
                else {
                    throw new Exception("Unknown gateway type: " + gateway.getType());
                }
            }
        }
        else if (dest instanceof MyTask) {
            //has exactly one incoming flow (currently being processed)
            //and exactly one outgoing flow
            MyTask task = (MyTask)dest;
            MyTransition transition = MyTransition.create(pnModel, task.getId(), task.getLabel(), false);
            MyPTArc.createAndBind(parentPlace, transition);

            //create place that follows the transition
            MyPlace newPlace = MyPlace.create(pnModel, task.getId(), "p" + placeLabelSequence++);
            MyTPArc.createAndBind(transition, newPlace);

            convertSequenceFlow(dest.getOutgoing().get(0), transition, newPlace, pnModel);
        }
    }
}