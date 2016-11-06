package ut.systems.modelling;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import ut.systems.modelling.converters.BpmnDiagramToBpmnModelConverter;
import ut.systems.modelling.converters.BpmnModelToPetriNetModelConverter;
import ut.systems.modelling.converters.PetriNetModelToPetrinetConverter;
import ut.systems.modelling.data.bpmn.*;
import ut.systems.modelling.data.petrinet.MyPetriNetModel;
import ut.systems.modelling.data.petrinet.MyWrappedPetrinet;

@Plugin(
        name = "Converter BPMN-PN",
        parameterLabels = { "BPMNDiagram" },
        returnLabels = { "Petri-Net" },
        returnTypes = { Petrinet.class },
        userAccessible = true,
        help = "Convert a BPMN diagram into a Petri-Net"
)
public class ConverterPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Taavi Ilp & Stepan Bolotnikov",
            email = "taaviilp@ut.ee & stepan@ut.ee"
    )
    @PluginVariant(variantLabel = "Convert BPMN into PN", requiredParameterLabels = {0})
    public static Petrinet optimizeDiagram(UIPluginContext context, BPMNDiagram diagram) throws Exception {
        MyBPMNModel bpmnModel = new BpmnDiagramToBpmnModelConverter().convert(diagram);
        MyPetriNetModel petriNetModel = new BpmnModelToPetriNetModelConverter().convert(bpmnModel);
        MyWrappedPetrinet wrappedPetriNet = new PetriNetModelToPetrinetConverter().convert(petriNetModel);
        context.addConnection(new InitialMarkingConnection(wrappedPetriNet.getPetrinet(), wrappedPetriNet.getInitialMarking()));
        return wrappedPetriNet.getPetrinet();
    }
}