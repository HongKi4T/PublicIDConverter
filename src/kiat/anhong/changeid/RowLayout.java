/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiat.anhong.changeid;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author honga
 */
public class RowLayout {
    private final Label label;
    private final TextField textField;
    private final Button button;
    
    public RowLayout(Label label, TextField textField, Button button){
        this.label = label;
        this.textField = textField;
        this.button = button;
    }

    public Label getLabel() {
        return label;
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }
    
}
