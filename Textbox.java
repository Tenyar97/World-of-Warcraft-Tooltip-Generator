import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Textbox extends JTextField 
{
    private String placeholder;

    public Textbox(String text) {
        super(text);
        this.placeholder = text;

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (getText().isEmpty() || getText().equals(placeholder)) 
                {
                    setText("");
                    setForeground(Color.BLACK);
                } else 
                {
                    setText(getText());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setForeground(Color.GRAY);
                    setText(placeholder);
                }
            }
        });

        if (getText().isEmpty()) {
            setForeground(Color.GRAY);
            setText(placeholder);
        }
    }
}