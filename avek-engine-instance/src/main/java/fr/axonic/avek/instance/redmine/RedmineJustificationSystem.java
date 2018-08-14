package fr.axonic.avek.instance.redmine;

import fr.axonic.avek.engine.JustificationSystem;
import fr.axonic.avek.engine.diagram.JustificationPatternDiagram;
import fr.axonic.avek.engine.exception.WrongEvidenceException;
import fr.axonic.avek.engine.pattern.DiagramPatternsBase;
import fr.axonic.avek.engine.pattern.Pattern;
import fr.axonic.avek.engine.pattern.type.InputType;
import fr.axonic.avek.engine.pattern.type.OutputType;
import fr.axonic.avek.engine.pattern.type.Type;
import fr.axonic.avek.engine.strategy.Strategy;
import fr.axonic.validation.exception.VerificationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedmineJustificationSystem extends JustificationSystem<DiagramPatternsBase> {

    private static final Type<RedmineDocumentEvidence> EVIDENCE_TYPE = new Type<>(RedmineDocumentEvidence.class);
    private static final Type<RedmineDocumentApproval> APPROVAL_TYPE = new Type<>(RedmineDocumentApproval.class);
    private static final Type<RedmineConclusion> CONCLUSION_TYPE = new Type<>(RedmineConclusion.class);

    public RedmineJustificationSystem() throws VerificationException, WrongEvidenceException {
        super(createPatternsBase());
        autoSupportFillEnable = true;
        versioningEnable = true;
    }

    private static DiagramPatternsBase createPatternsBase() {
        return new DiagramPatternsBase(createJPD());
    }

    private static JustificationPatternDiagram createJPD() {
        JustificationPatternDiagram jpd = new JustificationPatternDiagram();

        // TODO Missing: DE & feasibility
        InputType<RedmineDocumentEvidence> st0001 = new InputType<>("SWAM_ST_0001", EVIDENCE_TYPE);
        InputType<RedmineDocumentApproval> st0001Approval = new InputType<>("SWAM_ST_0001_APPROVAL", APPROVAL_TYPE);
        OutputType<RedmineConclusion> st0001Validated = new OutputType<>("SWAM_ST_0001 validated", CONCLUSION_TYPE);
        Strategy swamSt0001Strategy = new RedmineStrategy("Validate SWAM_ST_0001");
        jpd.addStep(new Pattern("SWAM_ST_0001_VALIDATION", "Validation of SWAM_ST_0001", swamSt0001Strategy,
                Arrays.asList(st0001, st0001Approval), st0001Validated));

        List<InputType> conclusionInputs = new ArrayList<>();
        for (int i = 2; i < 14; i++) {
            Pattern stXPattern = intermediaryST(i, st0001Validated.transformToInput());
            conclusionInputs.add(stXPattern.getOutputType().transformToInput());
            jpd.addStep(stXPattern);
        }

        OutputType<RedmineConclusion> stValidated = new OutputType<>("SWAM_ST_VALIDATED", CONCLUSION_TYPE);
        Strategy swamStStrategy = new RedmineStrategy("Validate SWAM technical specification");
        jpd.addStep(new Pattern("SWAM_ST_VALIDATION", "Validation of SWAM technical specification", swamStStrategy, conclusionInputs, stValidated));

        return jpd;
    }

    private static Pattern intermediaryST(int number, InputType<RedmineConclusion> swamSt0001IsValidated) {
        String fileName = "SWAM_ST_" + stNumber(number);

        InputType<RedmineDocumentEvidence> swamSt = new InputType<>(fileName, EVIDENCE_TYPE);
        InputType<RedmineDocumentApproval> swamStApproval = new InputType<>(fileName + "_APPROVAL", APPROVAL_TYPE);
        OutputType<RedmineConclusion> swamStValidated = new OutputType<>(fileName + " validated", CONCLUSION_TYPE);
        Strategy swamStStrategy = new RedmineStrategy("Validate " + fileName);

        return new Pattern(fileName + "_VALIDATION", "Validation of " + fileName, swamStStrategy,
                Arrays.asList(swamSt0001IsValidated, swamSt, swamStApproval), swamStValidated);
    }

    private static String stNumber(int number) {
        StringBuilder strNumber = new StringBuilder(Integer.toString(number));

        while (strNumber.length() < 4) {
            strNumber.insert(0, "0");
        }

        return strNumber.toString();
    }
}
