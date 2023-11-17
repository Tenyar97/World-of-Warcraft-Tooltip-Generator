import javax.swing.ImageIcon;

public class ImageIconFile 
{
        private final ImageIcon icon;
        private final String name;

        public ImageIconFile(ImageIcon icon, String name) 
        {
            this.icon = icon;
            this.name = name;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }