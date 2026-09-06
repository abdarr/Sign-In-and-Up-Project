package de.Abd.util;

import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

public class Ui {

   static public void setValidationStyle(

            TextInputControl field,
            boolean valid
    ) {
        if (field.getText().isBlank()) {
            field.setStyle("");
            return;
        }

        String borderColor = valid
                ? "#2E7D32"
                : "#E53935";

        field.setStyle(
                "-fx-border-color: " + borderColor + ";"
        );
    }

    public static void setToolTipButton(
            String colorCode,
            double width,
            TextInputControl field,
            String text
    ) {
        if (text == null || text.isBlank()) {
            field.setTooltip(null);
            return;
        }

        Tooltip tooltip = new Tooltip(text);

        tooltip.setWrapText(true);
        tooltip.setMaxWidth(width);
        tooltip.setShowDelay(null);
        tooltip.setStyle(
                "-fx-text-fill: " + colorCode + ";"
        );

        field.setTooltip(tooltip);
    }

    static private void setTooltipText(
            String colorCode,
            double width,
            Text text,
            String phrase

    ) {
        Tooltip tooltip =  new Tooltip();
        if (phrase == null || phrase.isBlank()) {
            return;
        }

       tooltip.setStyle("-fx-text-fill: #" + colorCode);
       tooltip.setText(phrase);
       Tooltip.install(text,tooltip);

    }








}
