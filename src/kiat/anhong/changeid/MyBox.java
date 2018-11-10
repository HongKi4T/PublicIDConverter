package kiat.anhong.changeId;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class MyBox extends HBox{
    public MyBox(){
        super(10);
        this.setAlignment(Pos.CENTER);
    }
    public void setButton(Button findButton){
        this.getChildren().add(findButton);
    }
}
