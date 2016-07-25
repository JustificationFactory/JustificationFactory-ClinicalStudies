package fr.axonic.avek.gui.model.structure;

import fr.axonic.avek.model.base.ARangedEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Nathaël N on 12/07/16.
 */
public class ExperimentationResults {
	private final Map<String, ARangedEnum> results;

	public ExperimentationResults() {
		results = new HashMap<>();
	}

	public void put(String ae1, ARangedEnum aEnum) {
		results.put(ae1, aEnum);
	}

	public List<ExpEffect> getList() {
		return results.keySet().stream()
				.map(s -> new ExpEffect(s, results.get(s)))
				.collect(Collectors.toList());
	}
}
