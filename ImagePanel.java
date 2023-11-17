import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(String imagePath) 
    {
        try (InputStream is = ImagePanel.class.getResourceAsStream("/" + imagePath)) 
        {
            if (is == null) 
            {
                throw new IOException("Cannot find resource: " + imagePath);
            }
            image = ImageIO.read(is);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        if (image != null) 
        {
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}