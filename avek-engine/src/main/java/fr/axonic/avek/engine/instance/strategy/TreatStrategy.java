package fr.axonic.avek.engine.instance.strategy;

import fr.axonic.avek.engine.conclusion.Conclusion;
import fr.axonic.avek.engine.evidence.Evidence;
import fr.axonic.avek.engine.instance.conclusion.ExperimentationConclusion;
import fr.axonic.avek.engine.instance.evidence.Stimulation;
import fr.axonic.avek.engine.instance.evidence.Subject;
import fr.axonic.avek.engine.strategy.ComputedStrategy;

import java.util.Map;


public class TreatStrategy extends ComputedStrategy {
	
	// createConclusion according to Evidences
	public Conclusion createConclusion(Map<String,Evidence> evidenceRoles){
		return new ExperimentationConclusion((Subject)evidenceRoles.get("subject").getElement(),(Stimulation)evidenceRoles.get("stimulation").getElement());
	}
	//	new Conclusion();
	//}

}