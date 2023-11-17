import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ImageIconFileRenderer extends DefaultListCellRenderer 
{
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
    {
        if (value instanceof ImageIconFile) 
        {
            ImageIconFile iconFile = (ImageIconFile) value;
            setIcon(iconFile.getIcon());
            setText(iconFile.getName());
        }
        return this;
    }
}