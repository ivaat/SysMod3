package ut.systems.modelling.converters;

import ut.systems.modelling.data.bpmn.*;
import ut.systems.modelling.data.petrinet.*;

/**
 * Created by taavii on 5.11.2016.
 */
public class BpmnModelToPetriNetModelConverter implements IConverter<MyBPMNModel, MyPetriNetModel> {
    private int placeLabelSequence = 1;
    private int transitionLabelSequence = 1;

    @Override
    public MyPetriNetModel convert(MyBPMNModel bpmnModel) throws Exception {
        MyPetriNetModel pnModel = MyPetriNetModel.create(bpmnModel.getLabel());

        MyPlace startPlace = MyPlace.createStartPlace(pnModel, bpmnModel.getStartEvent().getId(), bpmnModel.getStartEvent().getLabel());

        //start event must have only one outgoing flow
        convertSequenceFlow(bpmnModel.getStartEvent().getOutgoing().get(0), null, startPlace, pnModel);

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
            //if gateway has 1 incoming and 1 outgoing flow, we can basically remove this gateway
            if (dest.getOutgoing().size() == 1 && dest.getIncoming().size() == 1) {
                //this gateway is completely meaningless, let's skip it and process the subsequent flow
                convertSequenceFlow(dest.getOutgoing().get(0), prevTransition, parentPlace, pnModel);
            }
            else {
                MyGateway gateway = (MyGateway)dest;
                if (gateway.getType() == MyGateway.GatewayType.ANDSPLIT) {
                    //if previous non-place was startevent
                    //convert to new transition with several outgoings and handle it as such
                    if (pnModel.getStartPlace() == parentPlace) {
                        MyTransition transition = MyTransition.createAndBind(pnModel, dest.getId(), dest.getLabel(), parentPlace);
                        for (MySequenceFlow outgoing : gateway.getOutgoing()) {
                            //create arc & place first
                            MyPlace place = MyPlace.createAndBind(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++, transition);
                            convertSplitGatewaySequenceFlow(outgoing, transition, place, pnModel);
                        }
                    }
                    else {
                        //if previous non-place was transition
                        //transform the previous transition into this gateway
                        //for one sequence flow you already have made arc & place - current one
                        //so for first one just use existing one
                        //for all other flows create new transition & places and handle the sequence flows
                        boolean first = true;
                        for (MySequenceFlow outgoing: gateway.getOutgoing()) {
                            if (first) {
                                convertSplitGatewaySequenceFlow(outgoing, prevTransition, parentPlace, pnModel);
                                first = false;
                            }
                            else {
                                //create arc & place first
                                MyPlace place = MyPlace.createAndBind(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++, prevTransition);
                                convertSplitGatewaySequenceFlow(outgoing, prevTransition, place, pnModel);
                            }
                        }
                    }
                }
                else if (gateway.getType() == MyGateway.GatewayType.ANDJOIN) {
                    //convert to transition, tie all incoming arcs to this
                    MyTransition transition = pnModel.getTransitionById(gateway.getId());
                    if (transition == null) {
                        transition = MyTransition.createAndBind(pnModel, gateway.getId(), gateway.getLabel(), parentPlace);
                        MyPlace place = MyPlace.createAndBind(pnModel, gateway.getId(), "p" + placeLabelSequence++, transition);
                        convertSequenceFlow(gateway.getOutgoing().get(0), transition, place, pnModel);
                    }
                    else {
                        MyPTArc.createAndBind(parentPlace, transition);
                    }
                }
                else if (gateway.getType() == MyGateway.GatewayType.XORSPLIT) {
                    //from parentplace to each subsequent sequenceflow
                    if (parentPlace != pnModel.getStartPlace()) {
                        parentPlace.setLabel(gateway.getLabel());
                    }
                    for (MySequenceFlow outgoing : gateway.getOutgoing()) {
                        convertSplitGatewaySequenceFlow(outgoing, prevTransition, parentPlace, pnModel);
                    }
                }
                else if (gateway.getType() == MyGateway.GatewayType.XORJOIN) {
                    if (flow.getSource() instanceof MyGateway) {
                        //two gateways in a row - we need the dummy place & + new transition
                        MyTransition dummyTransition = MyTransition.createAndBind(pnModel, "t" + transitionLabelSequence, "t" + transitionLabelSequence++, parentPlace);

                        MyPlace xorPlace = pnModel.getPlaceById(gateway.getId());
                        if (xorPlace == null) {
                            xorPlace = MyPlace.createAndBind(pnModel, gateway.getId(), gateway.getLabel(), dummyTransition);
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
                            xorPlace = MyPlace.createAndBind(pnModel, gateway.getId(), gateway.getLabel(), prevTransition);
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
        else if (dest instanceof MySimpleTask) {
            //has exactly one incoming flow (currently being processed)
            //and exactly one outgoing flow
            MyTask task = (MyTask)dest;
            MyTransition transition = MyTransition.createAndBind(pnModel, task.getId(), task.getLabel(), parentPlace);

            //create place that follows the transition
            MyPlace newPlace = MyPlace.createAndBind(pnModel, task.getId(), "p" + placeLabelSequence++, transition);

            convertSequenceFlow(dest.getOutgoing().get(0), transition, newPlace, pnModel);
        }
        else if (dest instanceof MyCompoundTask) {
            MyCompoundTask task = (MyCompoundTask) dest;
            BpmnModelToPetriNetModelConverter converter = new BpmnModelToPetriNetModelConverter();
            MyPetriNetModel subModel = converter.convert(task.getNestedModel());

            //backtrack and remove the place creatde by previous step, replace it with start place of subprocess
            pnModel.getAllPlaces().remove(parentPlace);
            prevTransition.getOutgoingArcs().remove(prevTransition.getOutgoingArcByPlace(parentPlace));

            MyTPArc.createAndBind(prevTransition, pnModel.getStartPlace());

            pnModel.getAllPlaces().addAll(subModel.getAllPlaces());
            pnModel.getTransitions().addAll(subModel.getTransitions());

            convertSequenceFlow(dest.getOutgoing().get(0), null, subModel.getEndPlace(), pnModel);
        }
    }

    private void convertSplitGatewaySequenceFlow(MySequenceFlow outgoing, MyTransition prevTransition, MyPlace prevPlace, MyPetriNetModel pnModel) throws Exception {
        if (outgoing.getDestination() instanceof MyGateway) {
            //create dummy transition & place
            MyTransition dummyTransition = MyTransition.createAndBind(pnModel, "t" + transitionLabelSequence, "t" + transitionLabelSequence++, prevPlace);
            MyPlace dummyPlace = MyPlace.createAndBind(pnModel, "p" + placeLabelSequence, "p" + placeLabelSequence++, dummyTransition);
            convertSequenceFlow(outgoing, dummyTransition, dummyPlace, pnModel);
        }
        else {
            convertSequenceFlow(outgoing, prevTransition, prevPlace, pnModel);
        }
    }
}