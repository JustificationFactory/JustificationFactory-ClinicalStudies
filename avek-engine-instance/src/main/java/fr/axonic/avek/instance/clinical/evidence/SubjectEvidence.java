package fr.axonic.avek.instance.clinical.evidence;

import fr.axonic.avek.engine.support.evidence.Evidence;

/**
 * Created by cduffau on 16/03/17.
 */
public class SubjectEvidence extends Evidence<Subject>{
    public SubjectEvidence(String name, Subject element) {
        super(name, element);
    }

    public SubjectEvidence() {
        super();
    }
}
