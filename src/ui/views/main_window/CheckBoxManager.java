package ui.views.main_window;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class CheckBoxManager
{
    public VBox checkBoxVerticalBox;

    public CheckBoxManager(VBox _checkBoxVerticalBox)
    {
        checkBoxVerticalBox = _checkBoxVerticalBox;
    }

    public void checkBoxClicked(MouseEvent mouseEvent)
    {
        CheckBox clicked = (CheckBox) mouseEvent.getSource();
        for (Node node : checkBoxVerticalBox.getChildren())
        {
            CheckBox checkBox = (CheckBox) node;
            if (!checkBox.isSelected())
            {
                checkBox.setDisable(clicked.isSelected());
            }
        }
    }
}
