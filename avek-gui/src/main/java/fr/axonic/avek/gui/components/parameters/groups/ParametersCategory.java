package fr.axonic.avek.gui.components.parameters.groups;

import fr.axonic.avek.gui.components.parameters.IExpParameter;
import fr.axonic.avek.gui.components.parameters.leaves.CategoryTitle;
import fr.axonic.avek.gui.components.parameters.leaves.ExpParameterLeaf;
import fr.axonic.base.engine.*;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathaël N on 13/07/16.
 */
class ParametersCategory implements IExpParameter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParametersCategory.class);

    final int level;
    private ExpParameterLeaf title;

    private final List<IExpParameter> children;

    /**
     * @param level Deep level of this parameter grid (= his parent level+1)
     */
    ParametersCategory(final int level) {
        this.level = level;
        this.children = new ArrayList<>();
    }

    private void onClickExpand(boolean isExpanded) {
        children.forEach(lineList -> lineList.getNodes()
                .forEach(nodeList -> nodeList
                        .forEach(node -> {
                            /*ScaleTransition transition =
                                    new ScaleTransition(Duration.millis(500), node);
                            transition.setToY(isExpanded?1:0);
                            transition.setOnFinished(e -> {*/
                                 node.setVisible(isExpanded);
                                 node.setManaged(isExpanded);
                            /*});
                            transition.play();*/

        })));
    }


    /**
     * @param aEntity The AVar to addAndBind as a Experiment parameter,
     *                or the AList to addAndBind as a Experiment parameter sub group
     * @throws ClassCastException is the parameter is not a AVar
     *                            nor a AList (of AList and AVar)
     */
    private void addParameter(AEntity aEntity) {
        if(aEntity instanceof AStructure) {
            addCategory(AVarHelper.transformAStructureToAList((AStructure) aEntity));
        } else if(aEntity instanceof AList) {
            addCategory((AList) aEntity);
        } else if(aEntity instanceof AVar) {
            addLeaf((AVar) aEntity); // throws ClassCastException if not a AVar
        } else {
            LOGGER.error("Impossible cast object to AStruct, AList or AVar: "+aEntity);
        }
    }

    private void addCategory(AList list) {
        children.add(getNewCategory(list));
    }
    ParametersCategory getNewCategory(AList aList) {
        ParametersCategory subCategory = new ParametersCategory(level + 1);
        subCategory.setAList(aList);

        return subCategory;
    }

    private void addLeaf(AVar aVar) {
        children.add(getNewLeaf(aVar));
    }
    ExpParameterLeaf getNewLeaf(AVar aVar) {
        return new ExpParameterLeaf(level + 1, aVar);
    }


    ExpParameterLeaf generateTitle(String text) {
        return new CategoryTitle(level, text);
    }
    public final void setAList(AList<?> list) {
        // delete old children
        children.clear();

        // set new children
        list.getList().forEach(this::addParameter);

        title = generateTitle(list.getLabel());
        title.setExpandable(this::onClickExpand);
    }

    @Override
    public List<List<Node>> getNodes() {
        List<List<Node>> list = new ArrayList<>();

        // Add title nodes
        list.addAll(title.getNodes());

        // Add children nodes
        children.forEach(iep -> list.addAll(iep.getNodes()));
        return list;
    }
}
