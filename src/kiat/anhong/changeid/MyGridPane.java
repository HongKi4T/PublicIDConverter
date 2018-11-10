package kiat.anhong.changeId;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MyGridPane extends GridPane{
    public MyGridPane(){
        this.setupGrid();
    }
    
    private void setupGrid() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(20, 20, 0, 20));
    }
    public void setAllCol(ColumnConstraints columnConstraints){
        this.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints, new ColumnConstraints());
    }

    void add(RowLayout rowLayout, int i) {
        this.add(rowLayout.getLabel(), 0, i);
        this.add(rowLayout.getTextField(), 1, i);
        this.add(rowLayout.getButton(), 2, i);
    }
}
