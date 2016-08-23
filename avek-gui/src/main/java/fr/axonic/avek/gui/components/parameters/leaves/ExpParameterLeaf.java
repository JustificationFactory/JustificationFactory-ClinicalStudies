package fr.axonic.avek.gui.components.parameters.leaves;

import fr.axonic.avek.gui.components.parameters.IExpParameter;
import fr.axonic.avek.gui.util.LevelMark;
import fr.axonic.base.AContinuousNumber;
import fr.axonic.base.engine.AVar;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Nathaël N on 04/07/16.
 */
public class ExpParameterLeaf implements IExpParameter {
    // GUI Component
    final HBox paneTitle;
    final Label paramTitle;
    final Label paramValue;
    final LevelMark levelMark;
    final Label separator; // " : " label

    public ExpParameterLeaf(int level, AVar var) {
        // Init elements
        this.paramTitle = new Label(var.getLabel());
        this.paramValue = new Label();

        levelMark = new LevelMark(level);

        if(var.getValue() != null && !var.getValue().toString().equals("")) {
            // Pretty print dates
            if(var.getValue() instanceof Calendar) {
                SimpleDateFormat df = new SimpleDateFormat();
                df.applyPattern("dd/MM/yyyy HH:mm:ss");
                this.paramValue.setText(df.format(((Calendar)var.getValue()).getTime()));
            } else if(var instanceof AContinuousNumber) {
                this.paramValue.setText(var.getValue().toString()
                        + " "+ ((AContinuousNumber)var).getUnit());
            } else {
                this.paramValue.setText(var.getValue().toString());
            }
        }

        paneTitle = new HBox();
        paneTitle.getChildren().setAll(levelMark, paramTitle);
        separator = new Label(":");
        GridPane.setColumnIndex(paneTitle, 0);
        GridPane.setColumnIndex(separator, 1);
        GridPane.setColumnIndex(paramValue, 2);
    }


    protected List<Node> getNodeLine() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(paneTitle);

        if(!paramValue.getText().isEmpty()) {
            nodes.add(separator);
            nodes.add(paramValue);
        }

        return nodes;
    }
    @Override
    public final List<List<Node>> getNodes() {
        List<List<Node>> list = new ArrayList<>();
        list.add(getNodeLine());
        return list;
    }


    public void setExpandable(Consumer<Boolean> onClickExpand) {
        levelMark.setOnExpand(onClickExpand);
    }

    public void setExpanded(boolean expanded) {
        levelMark.setExpanded(expanded);
    }

    public Label getLabelTitle() {
        return paramTitle;
    }

}