package fr.axonic.avek.engine.constraint.graph;

import fr.axonic.avek.engine.support.SupportRole;
import fr.axonic.avek.engine.pattern.JustificationStep;
import fr.axonic.avek.engine.constraint.ArgumentationSystemConstraint;
import fr.axonic.avek.engine.constraint.PatternConstraintTest;
import fr.axonic.avek.engine.support.evidence.Hypothesis;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by cduffau on 14/03/17.
 */
public class NoHypothesisConstraintTest extends PatternConstraintTest{

    @Test
    public void testNoHypothesis(){
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoHypothesisConstraint();
        assertTrue(argumentationSystemConstraint.verify(argumentationSystem.getJustificationDiagram().getSteps()));
    }

    @Test
    public void testWithHypothesis(){
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoHypothesisConstraint();

        List<JustificationStep> stepList=argumentationSystem.getJustificationDiagram().getSteps();
        JustificationStep hyp=stepList.get(0);
        List<SupportRole> supportRoles =hyp.getSupports();
        SupportRole supportRole = supportRoles.get(0);
        supportRole.setSupport(new Hypothesis());
        supportRoles.set(0, supportRole);
        hyp.setSupports(supportRoles);
        stepList.set(0,hyp);

        assertFalse(argumentationSystemConstraint.verify(stepList));
    }



}