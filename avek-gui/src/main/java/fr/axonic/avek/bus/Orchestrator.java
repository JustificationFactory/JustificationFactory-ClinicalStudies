package fr.axonic.avek.bus;

import fr.axonic.avek.bus.translator.DataTranslator;
import fr.axonic.avek.engine.*;
import fr.axonic.avek.engine.exception.StepBuildingException;
import fr.axonic.avek.engine.exception.StrategyException;
import fr.axonic.avek.engine.exception.WrongEvidenceException;
import fr.axonic.avek.engine.support.Support;
import fr.axonic.avek.instance.clinical.conclusion.*;
import fr.axonic.avek.instance.clinical.evidence.Stimulation;
import fr.axonic.avek.instance.clinical.evidence.Subject;
import fr.axonic.avek.engine.pattern.Pattern;
import fr.axonic.avek.gui.api.ComponentType;
import fr.axonic.avek.gui.api.GUIAPI;
import fr.axonic.avek.gui.api.GUIException;
import fr.axonic.avek.gui.api.ViewType;
import fr.axonic.avek.gui.model.GUIExperimentParameter;
import fr.axonic.avek.model.MonitoredSystem;
import fr.axonic.base.engine.AEntity;
import fr.axonic.base.engine.AList;
import fr.axonic.base.engine.AStructure;
import fr.axonic.base.engine.AVarHelper;
import fr.axonic.validation.exception.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nathaël N on 04/08/16.
 */
public class Orchestrator implements Observer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Orchestrator.class);
    private static final String SUBJECT_STR = "subject";
    private static final String STIM_STR = "stimulation";
    private static final String PATTERN_TREAT = "Treat";
    private static final String PATTERN_ES_EF = "Establish Effect";
    private static final String PATTERN_GEN = "Generalize";

    private Pattern currentPattern;
    private Subject currentSubject;
    private AList<Effect> currentEffects;
    private Stimulation currentStimulation;

    private final List<Pattern> patternList;
    private List<Support> evidences;

    private final JustificationSystemAPI engineAPI;
    private final GUIAPI guiAPI;

    public Orchestrator(GUIAPI guiapi, JustificationSystemAPI engineAPI) throws VerificationException, WrongEvidenceException, GUIException {
        patternList = new ArrayList<>();

        this.engineAPI = engineAPI;
        guiAPI = guiapi;
        guiapi.addObserver(this);
        
        this.orchestrate();
    }

    /**
     * Compute for the next pattern to apply and show right view depending on
     * applicable patterns
     *
     * @throws GUIException
     * @throws VerificationException
     * @throws WrongEvidenceException
     */
    private void orchestrate() throws GUIException, VerificationException, WrongEvidenceException {
        computeNextPattern();

        // If there is only one pattern available, setting the view to it
        if (patternList.size() == 1) {
            showViewFromPattern(patternList.get(0));
        } else {
            Map<ComponentType, Object> content = new HashMap<>();

            content.put(ComponentType.SELECTION,
                    (List)(patternList.stream()
                            .map(Pattern::getName)
                            .collect(Collectors.toList())));

            guiAPI.show("Selection", ViewType.STRATEGY_SELECTION_VIEW, content);
        }
    }

    /**
     * Request engineAPI to get applicable patterns list
     */
    private void computeNextPattern() {
        // Preparing for following view
        evidences = engineAPI.getUnusedAssertions(evidences);
        patternList.clear();
        patternList.addAll(
                engineAPI.getPatternsBase().getPossiblePatterns(evidences)
                        .stream()
                        .map(engineAPI.getPatternsBase()::getPattern)
                        .collect(Collectors.toList()));
    }

    /**
     * Call GUI API to show the right view corresponding to a Pattern
     * @param pattern Will showing view corresponding to this pattern
     * @throws GUIException
     */
    private void showViewFromPattern(Pattern pattern) throws GUIException {
        ViewType viewType;

        Map<ComponentType, Object> content = getDataFromEvidence();
        Map<ComponentType, Object> newContent = new HashMap<>();
        content.forEach((k, v) -> newContent.put(k, DataTranslator.translateForGUI(v)));

        // Selecting the right view depending on pattern
        switch (pattern.getName()) {
            case PATTERN_TREAT:
                viewType = ViewType.TREAT_VIEW;
                break;
            case PATTERN_ES_EF:
                viewType = ViewType.ESTABLISH_EFFECT_VIEW;
                break;
            case PATTERN_GEN:
                viewType = ViewType.GENERALIZE_VIEW;
                break;
            default:
                throw new RuntimeException("Pattern is unknown for ViewType conversion: " + pattern);
        }

        guiAPI.show(pattern.getName(), viewType, newContent);
        currentPattern = pattern;
    }


    /**
     * Create a AList containing all EffectEnum values as Effect
     * @return the AList
     */
    private AList<Effect> generateEffectList() {
        try {
            List<Effect> list = new ArrayList<>();
            for (EffectEnum effectEnum : EffectEnum.values()) {
                Effect effect = new Effect();
                effect.setEffectValue(effectEnum);
                list.add(effect);
            }

            AList<Effect> alist = new AList<>();
            alist.addAll(list);

            return alist;
        }catch (VerificationException e) {
            throw new RuntimeException("Impossible to make AList<Effect>", e);
        }
    }

    /**
     * @return Map containing data present in current evidences
     */
    private Map<ComponentType, Object> getDataFromEvidence() {
        Map<ComponentType, Object> content = new HashMap<>();

        // Setting default Experiment results to Data bus
        currentEffects = generateEffectList();
        content.put(ComponentType.EFFECTS, currentEffects);

        // Setting others data to Data bus
        for (Support supportRole : evidences) {
            try {
                switch (supportRole.getName()) {
                    case SUBJECT_STR:
                        currentSubject = (Subject) supportRole.getElement();

                        MonitoredSystem ms = new MonitoredSystem(currentSubject.getId());
                        currentSubject.getFieldsContainer().forEach((key,val)-> {
                            if(val instanceof AStructure)
                                ms.addCategory(AVarHelper.transformAStructureToAList((AStructure) val));
                            else
                                LOGGER.warn("Not treated: '"+key+"':"+val);
                        });

                        content.put(ComponentType.MONITORED_SYSTEM, ms);
                        break;
                    case STIM_STR:
                        currentStimulation = (Stimulation) supportRole.getElement();

                        AList<AEntity> list = new AList<>();
                        list.setLabel("root");
                        list.addAll(currentStimulation.getFieldsContainer().values());

                        content.put(ComponentType.EXPERIMENTATION_PARAMETERS, new GUIExperimentParameter(list));
                        break;
                    default:
                        if (supportRole instanceof EstablishEffectConclusion) {
                            LOGGER.error("Got: " + supportRole);
                            EstablishEffectConclusion eec = (EstablishEffectConclusion) supportRole;

                            currentEffects = ((EstablishedEffect) eec.getElement()).getEffects();
                            content.put(ComponentType.EFFECTS, currentEffects);
                            break;
                        }
                        LOGGER.warn("Unknown Evidence role \"" + supportRole.getName() + "\" in " + supportRole);
                }
            } catch (RuntimeException e) {
                LOGGER.error("Impossible to treat Evidence role: " + supportRole, e);
            }
        }
        return content;
    }

    /**
     * Call the right constructXXXStep method depending on data from evidences
     * @param data Data from current evidences
     */
    private void constructStep(Map<ComponentType, Object> data) {
        // constructing step
        switch (currentPattern.getName()) {
            case PATTERN_TREAT:
                constructTreatStep();
                break;
            case PATTERN_ES_EF:
                constructEstablishEffectStep();
                break;
            case PATTERN_GEN:
                constructGeneralizeStep();
                break;
            default:
                LOGGER.error("Constructing \"" + currentPattern + "\" not implemented");
        }
    }

    private void constructTreatStep() {
        LOGGER.debug("Constructing Treat step");
        try {
            engineAPI.constructStep(currentPattern,
                    evidences,
                    new ExperimentationConclusion(
                            "Experimentation",
                            currentSubject,
                            currentStimulation));
        } catch (WrongEvidenceException | StepBuildingException | StrategyException e) {
            LOGGER.error("Impossible to constructStep");
        }
    }

    private void constructEstablishEffectStep() {
        LOGGER.debug("Constructing Establish effect step");
        try {
            //AList<Effect> effects = (AList<Effect>) DataTranslator.translateForEngine(data.get(ComponentType.EFFECTS));

            EstablishedEffect establishedEffect =
                    new EstablishedEffect(
                        new Experimentation(currentStimulation, currentSubject),
                        currentEffects);

            EstablishEffectConclusion conclusion =
                    new EstablishEffectConclusion(
                            "Establish Effect",
                            establishedEffect);

            // TODO pass UploadedFile.uploadedFolder; in establishEffectConclusion

            engineAPI.constructStep(currentPattern, evidences, conclusion);
        } catch (WrongEvidenceException | StepBuildingException | StrategyException e) {
            LOGGER.error("Impossible to constructStep");
        }
    }

    private void constructGeneralizeStep() {
        LOGGER.debug("Constructing Generalize step");
        try {

            // TODO pass UploadedFile.uploadedFolder; in GeneralizationConclusion
            //AList<Effect> effects = (AList<Effect>) DataTranslator.translateForEngine(data.get(ComponentType.EFFECTS));

            GeneralizationConclusion conclusion = new GeneralizationConclusion(
                    "Generalization",
                    new EstablishedEffect(
                            new Experimentation(
                                    currentStimulation,
                                    currentSubject),
                            currentEffects
                    ));

            engineAPI.constructStep(currentPattern, evidences, conclusion);
        } catch (WrongEvidenceException | StepBuildingException | StrategyException e) {
            LOGGER.error("Impossible to constructStep");
        }
    }


    /**
     *
     * @param o Should be a GUIAPI
     * @param arg Should be a Map&lt;FeedbackEventEnum, Object&gt;
     */
    @Override
    public void update(Observable o, Object arg) {
        if(!o.equals(guiAPI)) {
            throw new RuntimeException("Update get not from current used GUI API");
        }
        @SuppressWarnings("unchecked")
        Map<FeedbackEventEnum, Object> data = (Map<FeedbackEventEnum, Object>) arg;


        boolean strategyFlag = false;
        Map<ComponentType, Object> content = null;

        for(Map.Entry<FeedbackEventEnum,Object> entry : data.entrySet()) {
            switch (entry.getKey()) {
                // When user selected pattern strategy he wants clicking on submit button of selection view
                case PATTERN:
                    String selectedPatternName = (String) entry.getValue();

                    Optional<Pattern> pattern =
                            patternList.stream()
                                .filter(p -> p.getName().equals(selectedPatternName))
                                .findAny();

                    if(pattern.isPresent()) {
                        try {
                            showViewFromPattern(pattern.get());
                        } catch (GUIException e) {
                            LOGGER.error("Unknown error occurred while showing view", e);
                        }
                    } else {
                        LOGGER.warn("No pattern found with nameType: " + selectedPatternName);
                    }
                    break;

                // When user validate data he wrote clicking on strategy button
                case STRATEGY:
                    strategyFlag = true;
                    LOGGER.debug("Strategy flag raised");
                    break;

                case CONTENT:
                    //noinspection unchecked
                    content = (Map<ComponentType, Object>) data.get("Content");
                    LOGGER.debug("Content set to "+content);
                    break;

                case VIEW_TYPE: break;

                default:
                    throw new RuntimeException("No rule existing for nameType: " + entry.getKey());
            }
        }

        if(strategyFlag) {
            try {
                // Constructing conclusion
                constructStep(content);

                orchestrate();
            } catch (GUIException | VerificationException | WrongEvidenceException e) {
                LOGGER.error("Unknown error occurred while orchestrating", e);
            }
        }
    }
}
