package fr.axonic.avek.engine.constraint.graph;

import fr.axonic.avek.engine.exception.StepBuildingException;
import fr.axonic.avek.engine.exception.StrategyException;
import fr.axonic.avek.engine.exception.WrongEvidenceException;
import fr.axonic.avek.engine.support.Support;
import fr.axonic.avek.engine.support.conclusion.Conclusion;
import fr.axonic.avek.engine.constraint.ArgumentationSystemConstraint;
import fr.axonic.avek.engine.constraint.PatternConstraintTest;
import fr.axonic.avek.engine.support.evidence.Evidence;
import fr.axonic.avek.instance.conclusion.Effect;
import fr.axonic.avek.instance.conclusion.EstablishEffectConclusion;
import fr.axonic.avek.instance.conclusion.EstablishedEffect;
import fr.axonic.avek.instance.conclusion.ExperimentationConclusion;
import fr.axonic.avek.instance.evidence.Result;
import fr.axonic.avek.instance.evidence.ResultsEvidence;
import fr.axonic.avek.instance.evidence.Stimulation;
import fr.axonic.avek.engine.pattern.JustificationStep;
import fr.axonic.avek.engine.strategy.HumanStrategy;
import fr.axonic.avek.engine.strategy.Strategy;
import fr.axonic.avek.engine.pattern.type.InputType;
import fr.axonic.base.engine.AList;
import fr.axonic.validation.exception.VerificationException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by cduffau on 10/03/17.
 */
public class NoCycleConstraintTest extends PatternConstraintTest{

    @Override
    public void setUp() throws VerificationException, WrongEvidenceException, StrategyException, StepBuildingException {
        super.setUp();
        InputType<ExperimentationConclusion> rtExperimentation = new InputType<>("experimentation", ExperimentationConclusion.class);
        InputType<ResultsEvidence> rtResults = new InputType<>("result", ResultsEvidence.class);

        ResultsEvidence results0 = new ResultsEvidence("Result 0",new Result(new AList<>()));
        EstablishEffectConclusion effect0 = new EstablishEffectConclusion("Effect 0",new EstablishedEffect(null,new AList<>()));

        Support evExperimentation0 = rtExperimentation.create(experimentation0);
        Support evResults0 = rtResults.create(results0);
        argumentationSystem.constructStep(argumentationSystem.getPatternsBase().getPattern("2"), Arrays.asList(new Support[] {evExperimentation0,evResults0}), effect0);
    }

    @Test
    public void testCycleOneStair() throws VerificationException, WrongEvidenceException {
        Strategy strategy=new HumanStrategy();
        InputType evidenceRoleType1=new InputType("Test1", Evidence.class);
        Evidence evidence1=new Evidence("evidence1", new Stimulation());
        Support supportRole1 =evidenceRoleType1.create(evidence1);

        InputType evidenceRoleType2=new InputType("Test2", Evidence.class);
        Evidence evidence2=new Evidence("evidence2", new Stimulation());
        Support supportRole2 =evidenceRoleType2.create(evidence2);
        Conclusion conclusion=new Conclusion("conclusion",new Effect());

        InputType conclusionRoleType2=new InputType("Test2", Conclusion.class);
        Support conclusionRole=conclusionRoleType2.create(conclusion);

        JustificationStep step1=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole), conclusion);
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoCycleConstraint(step1);

        assertFalse(argumentationSystemConstraint.verify(Arrays.asList(step1)));
    }

    @Test
    public void testCycleTwoStair() throws VerificationException, WrongEvidenceException {
        Strategy strategy=new HumanStrategy();
        InputType evidenceRoleType1=new InputType("Test1", Evidence.class);
        Evidence evidence1=new Evidence("evidence1", new Stimulation());
        Support supportRole1 =evidenceRoleType1.create(evidence1);

        InputType evidenceRoleType2=new InputType("Test2", Evidence.class);
        Evidence evidence2=new Evidence("evidence2", new Stimulation());
        Support supportRole2 =evidenceRoleType2.create(evidence2);
        Conclusion conclusion=new Conclusion("conclusion1",new Effect());
        Conclusion conclusion2=new Conclusion("conclusion2",new Effect());

        InputType conclusionRoleType2=new InputType("Test3", Conclusion.class);
        Support supportRole3 =conclusionRoleType2.create(conclusion);
        InputType conclusionRoleType3=new InputType("Test4", Conclusion.class);
        Support supportRole4 =conclusionRoleType3.create(conclusion2);


        JustificationStep step1=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, supportRole4), conclusion);
        JustificationStep step2=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, supportRole3), conclusion2);
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoCycleConstraint(step1);

        assertFalse(argumentationSystemConstraint.verify(Arrays.asList(step1, step2)));
    }

    @Test
    public void testCycleTwoStairAndSplit() throws VerificationException, WrongEvidenceException {
        Strategy strategy=new HumanStrategy();
        InputType evidenceRoleType1=new InputType("Test1", Evidence.class);
        Evidence evidence1=new Evidence("evidence1", new Stimulation());
        Support supportRole1 =evidenceRoleType1.create(evidence1);

        InputType evidenceRoleType2=new InputType("Test2", Evidence.class);
        Evidence evidence2=new Evidence("evidence2", new Stimulation());
        Support supportRole2 =evidenceRoleType2.create(evidence2);
        Conclusion conclusion=new Conclusion("conclusion1",new Effect());
        Conclusion conclusion2=new Conclusion("conclusion2",new Effect());
        Conclusion conclusion3=new Conclusion("conclusion3",new Effect());

        InputType conclusionRoleType2=new InputType("Test3", Conclusion.class);
        Support conclusionRole1=conclusionRoleType2.create(conclusion);
        InputType conclusionRoleType3=new InputType("Test4", Conclusion.class);
        Support conclusionRole2=conclusionRoleType3.create(conclusion2);


        JustificationStep step1=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole2), conclusion);
        JustificationStep step2=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole1), conclusion3);
        JustificationStep step3=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole1), conclusion2);
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoCycleConstraint(step1);

        assertFalse(argumentationSystemConstraint.verify(Arrays.asList(step1, step2, step3)));
    }

    @Test
    public void testNoCycle() {
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoCycleConstraint((JustificationStep) argumentationSystem.getJustificationDiagram().getSteps().get(0));
        assertTrue(argumentationSystemConstraint.verify(argumentationSystem.getJustificationDiagram().getSteps()));
    }

    @Test
    public void testNoCycleTwoStairAndEvidenceReuse() throws VerificationException, WrongEvidenceException {
        Strategy strategy=new HumanStrategy();
        InputType evidenceRoleType1=new InputType("Test1", Evidence.class);
        Evidence evidence1=new Evidence("evidence1", new Stimulation());
        Support supportRole1 =evidenceRoleType1.create(evidence1);

        InputType evidenceRoleType2=new InputType("Test2", Evidence.class);
        Evidence evidence2=new Evidence("evidence2", new Stimulation());
        Support supportRole2 =evidenceRoleType2.create(evidence2);
        Conclusion conclusion=new Conclusion("conclusion1",new Effect());
        Conclusion conclusion2=new Conclusion("conclusion2",new Effect());
        Conclusion conclusion3=new Conclusion("conclusion3",new Effect());

        InputType conclusionRoleType2=new InputType("Test3", Conclusion.class);
        Support conclusionRole1=conclusionRoleType2.create(conclusion);
        InputType conclusionRoleType3=new InputType("Test4", Conclusion.class);
        Support conclusionRole2=conclusionRoleType3.create(conclusion2);


        JustificationStep step1=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2), conclusion);
        JustificationStep step2=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole1), conclusion3);
        JustificationStep step3=new JustificationStep("1",strategy,Arrays.asList(supportRole1, supportRole2, conclusionRole1), conclusion2);
        ArgumentationSystemConstraint argumentationSystemConstraint =new NoCycleConstraint(step1);

        assertTrue(argumentationSystemConstraint.verify(Arrays.asList(step1, step2, step3)));
    }



}