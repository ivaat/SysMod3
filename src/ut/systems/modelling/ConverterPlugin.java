package ut.systems.modelling;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

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
            email = "taaviilp@ut.ee & STEPANPUTYOUREMAILHERE"
    )
    @PluginVariant(variantLabel = "Convert BPMN into PN", requiredParameterLabels = {0})
    public static Petrinet optimizeDiagram(UIPluginContext context, BPMNDiagram diagram) {
        Petrinet pn = null;

//        MyBPMNModel myBPMNModel = getMyBPMNModel(diagram);
//        pn = MyConverter.getPN(myBPMNModel);

        return pn;
    }
}
