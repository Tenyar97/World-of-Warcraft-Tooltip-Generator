import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Window.Type;
import javax.swing.Box;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.awt.Canvas;
import java.awt.Button;

public class Main {

    private static JFrame mainFrame;
    private JButton spellIcon = new JButton();
    private static final int ORIG_WIDTH = 450;
    private static final int ORIG_HEIGHT = 156;
    private static Random rand = new Random();
    
    public static final String configFile = "config.txt";
    private final List<ImageIconFile> mruImages = new ArrayList<>();
    private static List<ImageIconFile> cachedImageList = new ArrayList<>();
    public static int mruLimit = 10;
    public static String defaultSavePath = System.getProperty("user.home");
    
    private static final String ICONS_DIRECTORY = "Icons";
    private static final String COOLDOWN_SUFFIX = " cooldown";
    private static final String DEFAULT_COOLDOWN = "Cooldown";
    
    private static final int COOLDOWN_LABEL_X_DEFAULT = 350;
    private static final int COOLDOWN_LABEL_X_ALTERNATE = 280;
    private static final int COOLDOWN_LABEL_Y = 65;
    private static final int COOLDOWN_LABEL_WIDTH = 200;
    private static final int COOLDOWN_LABEL_HEIGHT = 23;
    private static final int DESCRIPTION_PADDING_EXTRA = 10;

    private final JTextArea descriptionText = new JTextArea("Description");
    private final JTextPane descriptionBox = new JTextPane();
    private final JComboBox<String> classModel = new JComboBox<>();
    private final JComboBox<Integer> levelModel = new JComboBox<>();
    private final JMenuBar menuBar = new JMenuBar();
    private final ButtonGroup resourceGroup = new ButtonGroup();

    private final Font NAME_FONT = new Font("Sans-serif", Font.BOLD, 20);
    private final Font INFO_FONT = new Font("Sans-serif", Font.BOLD, 14);
    private final Font DESCRIPTION_FONT = new Font("Sans-serif", Font.PLAIN, 13);
    private final Color GRAY = new Color(84, 84, 84);
    
    private static final JLabel spellNameLabel = createLabel("");
    private static final JLabel resourceCostLabel = createLabel("");
    private static final JLabel castTimeLabel = createLabel("");
    private static final JLabel rankLabel = createLabel("");
    private static final JLabel rangeLabel = createLabel("");
    private static final JLabel talentLabel = createLabel("Talent");
    private static final JLabel requirementLabel= createLabel("Required ");
    private static final JLabel classLabel = createLabel("");
    private static final JLabel levelLabel = createLabel("");
    private final JLabel cooldownLabel = createLabel("");

    private final Textbox spellNameText = new Textbox("Spell Name");
    private final Textbox resourceCostText = new Textbox("Resource Cost");
    private final Textbox castTimeText = new Textbox("Cast Time");
    private final Textbox rankText = new Textbox("Rank");
    private final Textbox rangeText = new Textbox("Range");
    private final Textbox cooldownTimeText = new Textbox("Cooldown");

    private final JRadioButton manaButton = new JRadioButton("Mana");
    private final JRadioButton rageButton = new JRadioButton("Rage");
    private final JRadioButton energyButton = new JRadioButton("Energy");
    private static final JCheckBox talentBox = new JCheckBox("Is Talent");
    private final JCheckBox classBox = new JCheckBox("Class Requirement");
    private static final JCheckBox levelBox = new JCheckBox("Level Requirement");
    private static int secretCounter = 0;
    private String resource = "";

    public Main() throws IOException 
    {
        mainFrame = initializeMainFrame();
        ImagePanel panel = initializeImagePanel();

        setupSpellIcon(spellIcon, panel);
        setupLabels(panel);
        setupDescriptionBox(panel);

        readConfiguration();
        finalizeMainFrameSetup();
        openEditWindow();
        descriptionText.requestFocus();
        updateLabels();
    }

    private JFrame initializeMainFrame() 
    {
        JFrame frame = new JFrame("Tooltip Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(ORIG_WIDTH, ORIG_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getLocation().x, frame.getLocation().y + 175);
        return frame;
    }

    private ImagePanel initializeImagePanel() throws IOException 
    {
        ImagePanel panel = new ImagePanel("Tooltip_Box.png");
        panel.setLayout(null);
        mainFrame.getContentPane().add(panel);
        return panel;
    }

    private void setupLabels(JPanel panel) 
    {
        JLabel[] labels = { spellNameLabel, talentLabel, requirementLabel, classLabel, levelLabel, resourceCostLabel, castTimeLabel, cooldownLabel, rankLabel, rangeLabel };
        Font[] fonts = { NAME_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT, INFO_FONT };
        Color[] colors = { Color.WHITE, GRAY, GRAY, GRAY, GRAY, Color.WHITE, Color.WHITE, Color.WHITE, GRAY, GRAY };
        int[][] bounds = { { 84, 5, 200, 40 }, { 87, 30, 200, 23 },{ 87, 17, 200, 23 }, { 87, 43, 200, 23 }, { 87, 55, 200, 23 },{ 87, 70, 200, 23 }, { 87, 85, 200, 23 },{ 285, 85, 200, 23 }, { 355, 15, 200, 23 }, { 340, 40, 200, 23 } };
        boolean[] visibility = { true, false, false, false, false, true, true, true, true, true };

        for (int i = 0; i < labels.length; i++) 
        {
            setupLabel(labels[i], fonts[i], colors[i], bounds[i], visibility[i]);
            panel.add(labels[i]);
        }
    }

    private void setupLabel(JLabel label, Font font, Color color, int[] bounds, boolean isVisible) 
    {
        label.setFont(font);
        label.setForeground(color);
        label.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        label.setVisible(isVisible);
    }

    private void setupDescriptionBox(JPanel panel)
    {
        descriptionBox.setFont(DESCRIPTION_FONT);
        descriptionBox.setForeground(Color.YELLOW);
        descriptionBox.setOpaque(false);
        descriptionBox.setBounds(8, 100, 418, 25);
        panel.add(descriptionBox);
    }

    private void readConfiguration() throws IOException 
    {
        ConfigReader.readConfig(configFile);
    }

    private void finalizeMainFrameSetup() 
    {
        mainFrame.setVisible(true);
    }

    private static void setX(Component comp, int x)
    {
    	comp.setBounds(x, comp.getY(), comp.getWidth(), comp.getHeight());
    }
    
    private static void setY(Component comp, int y)
    {
    	comp.setBounds(comp.getX(), y, comp.getWidth(), comp.getHeight());
    }
    
    private void openEditWindow() 
    {
        JFrame editFrame = createEditFrame();
        setupMenuBar(editFrame);
        setupTextComponents(editFrame);
        setupRadioButtons(editFrame);
        setupComboBoxes(editFrame);
        setupActionListeners();
        setupDocumentListeners(editFrame);
        setupWindowClosingBehaviour(editFrame);
        editFrame.setVisible(true);
    }

    private JFrame createEditFrame() 
    {
        JFrame editFrame = new JFrame("Edit Tooltip");
        editFrame.setSize(400, 524);
        editFrame.setLocation(mainFrame.getX() / 2 + 683, mainFrame.getY() / 2 - 98);
        editFrame.getContentPane().setLayout(null);
        
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return editFrame;
    }

    private void setupMenuBar(JFrame frame) 
    {
        JMenu menu = new JMenu("Options");
        JMenuItem saveItem = new JMenuItem("Save as PNG");
        JMenuItem settings = new JMenuItem("Settings");

        saveItem.addActionListener(e -> saveFrameAsImage(mainFrame, spellNameLabel, defaultSavePath));
        settings.addActionListener(e -> openSettingsDialog(e));

        menu.add(saveItem);
        menu.add(settings);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    private void setupTextComponents(JFrame frame) 
    {
        JTextField[] textFields = { spellNameText, resourceCostText, castTimeText, rankText, rangeText };
        int[] yPos = { 11, 42, 104, 135, 166, 104 };
        
        for (int i = 0; i < textFields.length; i++) 
        {
            setupTextField(textFields[i], 10, yPos[i], 166, 20);
            frame.getContentPane().add(textFields[i]);
        }
        setupTextField(cooldownTimeText, 185, 104, 166, 20);
        frame.getContentPane().add(cooldownTimeText);
        
        descriptionText.setLineWrap(true);
        
        descriptionText.setWrapStyleWord(true);
        descriptionText.setText("Description");
        JScrollPane scrollPane = new JScrollPane(descriptionText);
        scrollPane.setBounds(10, 310, 350, 142);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.getContentPane().add(scrollPane);
    }

    private void setupTextField(JTextField textField, int x, int y, int width, int height) 
    {
        textField.setColumns(20);
        textField.setBounds(x, y, width, height);
    }

    private void setupRadioButtons(JFrame frame) 
    {
        JRadioButton[] buttons = { manaButton, rageButton, energyButton };
        String[] commands = { "Mana", "Rage", "Energy" };
        int[] xPos = { 20, 155, 277 };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setActionCommand(commands[i]);
            buttons[i].setBounds(xPos[i], 69, 70, 23);
            resourceGroup.add(buttons[i]);
            buttons[i].setEnabled(true);
            frame.getContentPane().add(buttons[i]);
        }
    }

    private void setupComboBoxes(JFrame frame) 
    {
        talentBox.setBounds(10, 201, 97, 23);
        classBox.setBounds(10, 227, 150, 23);
        levelBox.setBounds(10, 253, 150, 23);
        classModel.setModel(new DefaultComboBoxModel<>(new String[]{"Druid", "Hunter", "Mage", "Paladin", "Priest", "Rogue", "Shaman", "Warlock", "Warrior"}));
        classModel.setBounds(169, 227, 83, 22);
        classModel.setVisible(false);

        frame.getContentPane().add(talentBox);
        frame.getContentPane().add(classBox);
        frame.getContentPane().add(levelBox);
        frame.getContentPane().add(classModel);

        for (int i = 1; i <= 60; i++) {
            levelModel.addItem(i);
        }
        levelModel.setBounds(170, 253, 82, 22);
        levelModel.setVisible(false);
        frame.getContentPane().add(levelModel);
    }

    private void setupActionListeners() 
    {
        ActionListener radioListener = e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            resource = button.getText();
            boolean resourceCostPhrase = resourceCostText.getText().matches(".*[a-zA-Z]+.*");
            if (resourceCostPhrase) {
                resourceCostLabel.setText(resourceCostText.getText() + " " + resource.toLowerCase());
            }
        };

        manaButton.addActionListener(radioListener);
        rageButton.addActionListener(radioListener);
        energyButton.addActionListener(radioListener);

        classBox.addActionListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            classModel.setVisible(cb.isSelected());
            classLabel.setVisible(cb.isSelected());
            if (cb.isSelected()) {
                updateClassRequirement(classModel);
            }
        });

        levelBox.addActionListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            levelModel.setVisible(cb.isSelected());
            levelLabel.setVisible(cb.isSelected());
            if (cb.isSelected()) {
            	toggleLevelRequirementVisibility(levelModel);
            }
        });

        talentBox.addActionListener(e -> toggleTalentVisibility());
        classModel.addActionListener(e -> updateClassRequirement(classModel));
        levelModel.addActionListener(e -> toggleLevelRequirementVisibility(levelModel));
    }

    private void setupDocumentListeners(JFrame frame) 
    {
        DocumentListener documentListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateLabels();
            }
            public void removeUpdate(DocumentEvent e) {
                updateLabels();
            }
            public void insertUpdate(DocumentEvent e) {
                updateLabels();
            }
        };

        JTextField[] textFields = { spellNameText, resourceCostText, castTimeText, rankText, rangeText, cooldownTimeText };
        for (JTextField textField : textFields) {
            textField.getDocument().addDocumentListener(documentListener);
        }
        descriptionText.getDocument().addDocumentListener(documentListener);
    }

    private void setupWindowClosingBehaviour(JFrame frame)
    {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                mainFrame.dispose();
            }
        });
    }
	


    private void openSettingsDialog(ActionEvent e) 
    {
        JDialog settingsDialog = initializeSettingsDialog();
        addMRUSettings(settingsDialog);
        addDefaultPathSettings(settingsDialog);
        addAuthorInfo(settingsDialog);
        finalizeDialog(settingsDialog);
    }

    private JDialog initializeSettingsDialog() 
    {
        JDialog settingsDialog = new JDialog(mainFrame, "Settings", true);
        settingsDialog.setType(Type.UTILITY);
        settingsDialog.setResizable(false);
        settingsDialog.getContentPane().setLayout(new GridLayout(3, 1));
        return settingsDialog;
    }

    private void addMRUSettings(JDialog dialog) 
    {
        dialog.getContentPane().add(new JLabel("Most recently used limit:"));
        JTextField mruLimitField = new JTextField(Integer.toString(mruLimit), 10);
        dialog.getContentPane().add(mruLimitField);

        JButton saveMruLimitButton = createSaveMruLimitButton(mruLimitField, dialog);
        dialog.getContentPane().add(saveMruLimitButton);
    }

    private JButton createSaveMruLimitButton(JTextField mruLimitField, JDialog dialog) 
    {
        JButton button = new JButton("Save MRU Limit");
        button.addActionListener(event -> {
            try {
                int parsedLimit = Integer.parseInt(mruLimitField.getText());
                mruLimit = parsedLimit;
                try {
					ConfigReader.updateConfig(configFile, ConfigReader.MRULIMIT, String.valueOf(mruLimit));
				} catch (IOException e) {
					e.printStackTrace();
				}
                JOptionPane.showMessageDialog(dialog, "Will save the last " + parsedLimit + " icons for easy re-selection!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for MRU limit.");
            }
        });
        return button;
    }

    private void addDefaultPathSettings(JDialog dialog)
    {
        dialog.getContentPane().add(new JLabel("Default Save Path:"));
        JTextField defaultPathField = new JTextField(defaultSavePath.replace("=", ""), 20);
        dialog.getContentPane().add(defaultPathField);

        JButton saveDefaultPathButton = createSaveDefaultPathButton(defaultPathField, dialog);
        dialog.getContentPane().add(saveDefaultPathButton);
    }

    private JButton createSaveDefaultPathButton(JTextField defaultPathField, JDialog dialog) 
    {
        JButton button = new JButton("Save Default Path");
        button.addActionListener(event -> {
            defaultSavePath = defaultPathField.getText().replaceAll("=", "");
            try {
				ConfigReader.updateConfig(configFile, ConfigReader.DEFAULTSAVE, defaultSavePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
            JOptionPane.showMessageDialog(dialog, "Default save path set to: " + defaultPathField.getText());
        });
        return button;
    }

    private void addAuthorInfo(JDialog dialog) 
    {
        Component horizontalStrut = Box.createHorizontalStrut(20);
        dialog.getContentPane().add(horizontalStrut);

        JTextField authorField = createAuthorField();
        dialog.getContentPane().add(authorField);
    }

    private JTextField createAuthorField() 
    {
        JTextField authorField = new JTextField("Tenyar97");
        authorField.setBorder(null);
        authorField.setBackground(null);
        authorField.setEditable(false);
        return authorField;
    }

    private void finalizeDialog(JDialog dialog)
    {
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void updateLabels()
    {
        updateLabel(spellNameLabel, spellNameText);
        updateLabel(castTimeLabel, castTimeText);
        updateLabel(rankLabel, rankText);
        updateLabel(rangeLabel, rangeText);
        updateDescriptionBox();
        updateRangeLabel();
        updateCooldownLabel();
        updateResourceCostLabel();
        resizeDescriptionBox();
    }

	private void updateLabel(JLabel label, JTextField textField)
	{
        label.setText(textField.getText());
    }

    private void updateRangeLabel() 
    {
    	int rangeOffset = rangeText.getText().equals("Range") ? 350 : 340;
    	
    	setX(rangeLabel, rangeOffset);
	}
	
    private void updateCooldownLabel() 
    {
        String cooldownText = cooldownTimeText.getText();
        boolean isDefaultCooldown = cooldownText.isEmpty() || cooldownText.equals(DEFAULT_COOLDOWN);
        
        cooldownLabel.setText(isDefaultCooldown ? DEFAULT_COOLDOWN : cooldownText + COOLDOWN_SUFFIX);
        int xPosition = isDefaultCooldown ? COOLDOWN_LABEL_X_DEFAULT : COOLDOWN_LABEL_X_ALTERNATE;
        cooldownLabel.setBounds(xPosition, COOLDOWN_LABEL_Y, COOLDOWN_LABEL_WIDTH, COOLDOWN_LABEL_HEIGHT);
    }

    private void updateResourceCostLabel() 
    {
        boolean resourceCostPhrase = resourceCostText.getText().matches(".*[a-zA-Z]+.*");
        String resourceText = resourceCostPhrase ? resource.toLowerCase() : resource;
        resourceCostLabel.setText(resourceCostText.getText() + " " + resourceText);
    }
    
    private void resizeDescriptionBox() 
    {
        int fixedWidth = descriptionBox.getWidth();
        Insets insets = descriptionBox.getInsets();
        int padding = insets.top + insets.bottom;

        JTextArea tempTextArea = new JTextArea(descriptionBox.getText());
        tempTextArea.setFont(descriptionBox.getFont());
        tempTextArea.setLineWrap(true);
        tempTextArea.setWrapStyleWord(true);
        tempTextArea.setSize(fixedWidth, Integer.MAX_VALUE);

        int requiredHeight = tempTextArea.getPreferredSize().height + padding + DESCRIPTION_PADDING_EXTRA;
        int heightDifference = requiredHeight - descriptionBox.getHeight();

        resizeComponent(descriptionBox, fixedWidth, requiredHeight);
        resizeMainFrame(heightDifference);
    }

    private void resizeComponent(Component component, int width, int height) 
    {
        component.setBounds(component.getX(), component.getY(), width, height);
    }

    private void resizeMainFrame(int heightDifference) 
    {
        if (heightDifference != 0)
        {
            int newMainFrameHeight = Math.max(mainFrame.getHeight() + heightDifference, ORIG_HEIGHT);
            mainFrame.setSize(mainFrame.getWidth(), newMainFrameHeight);
        }
    }
    
    private void updateDescriptionBox() 
    {
        descriptionBox.setText(descriptionText.getText());
    }

    private static void toggleTalentVisibility()
    {
        boolean isSelected = talentBox.isSelected();
        
        if (talentLabel.isVisible() != isSelected) 
        {
            talentLabel.setVisible(isSelected);
        }
        doSuperSecret();
    }
    
    private static void doSuperSecret()
    {
    	
    	JDialog dialog = createDialog();
    	int secretNumber = rand.nextInt(6);
    	ImagePanel sloth = new ImagePanel("\\Icons\\secrets\\secretSloth" + secretNumber + ".png");
    	
    	
    	
    	sloth.setVisible(true);
        dialog.setVisible(false);
        dialog.setTitle("Super Secret");
        dialog.getContentPane().add(sloth);
        
    	
        
    	secretCounter++;
    	System.out.println(secretCounter);
    	if(secretCounter == 15)
    	{
    		System.out.println("Secret number: " + secretNumber);
    		dialog.setVisible(true);
    		secretCounter = 0;
    	}
    	
    }

    private static void updateClassRequirement(JComboBox<String> comboBox) 
    {
        classLabel.setText(requirementLabel.getText() + comboBox.getSelectedItem().toString());
        classLabel.setVisible(true);
    }

    private static void toggleLevelRequirementVisibility(JComboBox<Integer> comboBox)
    {
        boolean isSelected = levelBox.isSelected();
        
            levelLabel.setVisible(isSelected);
            
            if (isSelected) 
            {
                levelLabel.setText(requirementLabel.getText() + comboBox.getSelectedItem().toString());
            
            }
    }
    
    private void saveFrameAsImage(JFrame frame, JLabel spellNameLabel, String defaultSavePath) 
    {
        BufferedImage image = captureFrameAsImage(frame);
        File fileToSave = promptUserForFileLocation(frame, spellNameLabel, defaultSavePath);

        if (fileToSave != null) {
            saveImageToFile(frame, image, fileToSave);
        }
    }
    
	private BufferedImage captureFrameAsImage(JFrame frame) 
	{
	    Rectangle frameBounds = frame.getBounds();
	    BufferedImage image = new BufferedImage(frameBounds.width, frameBounds.height, BufferedImage.TYPE_INT_ARGB);
	    Graphics graphics = image.getGraphics();
	    try {
	        frame.paint(graphics);
	    } finally {
	        graphics.dispose();
	    }
	    return image;
	}

	private File promptUserForFileLocation(JFrame frame, JLabel spellNameLabel, String defaultSavePath) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Specify where to save your Tooltip");

	    // Set the default directory or file
	    File defaultPath = new File(defaultSavePath);
	    if (defaultPath.isDirectory()) {
	        fileChooser.setCurrentDirectory(defaultPath);
	    } else {
	        fileChooser.setCurrentDirectory(defaultPath.getParentFile());
	    }

	    // Set the default file name
	    String defaultFileName = spellNameLabel.getText() + "_tooltip.png";
	    fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), defaultFileName));

	    int userSelection = fileChooser.showSaveDialog(frame);
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        return ensurePngExtension(selectedFile);
	    }
	    
	    return null;
	}

	private void setFileChooserDefaultPath(JFileChooser fileChooser, String defaultSavePath) 
	{
	    File defaultPath = new File(defaultSavePath);
	    
	    if (defaultPath.isDirectory()) 
	    {
	        fileChooser.setCurrentDirectory(defaultPath);
	    } 
	    else
	    {
	        fileChooser.setSelectedFile(defaultPath);
	    }
	}

	private File ensurePngExtension(File file)
	{
	    if (!file.getAbsolutePath().endsWith(".png")) 
	    {
	        return new File(file.getAbsolutePath() + ".png");
	    }
	    
	    return file;
	}

	private void saveImageToFile(JFrame frame, BufferedImage image, File fileToSave) 
	{
	    try 
	    {
	        ImageIO.write(image, "png", fileToSave);
	        JOptionPane.showMessageDialog(frame, "Tooltip successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
	    } 
	    catch (IOException ex) 
	    {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(frame, "Error saving frame as PNG: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
    
	private void setupSpellIcon(JButton button, Container container) throws IOException 
	{
		int BUTTON_X = 15;
		int BUTTON_Y = 15;
		int BUTTON_WIDTH = 60;
		int BUTTON_HEIGHT = 60;
		
	    button.addActionListener(this::openIconSelectionDialog);
	    button.setBounds(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
	    button.setToolTipText("Click to change Spell Icon.");
	    button.setMargin(new Insets(0, 0, 0, 0));
	    button.setBorder(BorderFactory.createEmptyBorder());
	    button.setContentAreaFilled(false);
	    button.setBorderPainted(false);
	    button.setOpaque(false);
	    container.add(button);

	    ImageIcon defaultImage = loadDefaultSpellIcon();
	    
	    if (defaultImage != null) 
	    {
	        button.setIcon(defaultImage);
	        button.setPreferredSize(new Dimension(defaultImage.getIconWidth(), defaultImage.getIconHeight()));
	    }
	}


	private ImageIcon loadDefaultSpellIcon() 
	{
		String ICONS_DIRECTORY = "Icons";
	    File[] iconFiles = Optional.ofNullable(new File(ICONS_DIRECTORY).listFiles())
	                               .orElse(new File[0]);

	    if (iconFiles.length > 0) 
	    {
	        try {
	            return new ImageIcon(ImageIO.read(iconFiles[0]));
	        } 
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }
	    return null;
	}
    
	private void openIconSelectionDialog(ActionEvent e) 
	{
        JDialog dialog = createDialog();
        JPanel loadingPanel = createLoadingPanel(dialog);
        loadIconsAsync(dialog, loadingPanel);
        dialog.setVisible(true);
    }

    private static JDialog createDialog() 
    {
        JDialog dialog = new JDialog(mainFrame, "Select a Spell Icon", true);
        dialog.setSize(400, 425);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setLocation(mainFrame.getX() + 50, mainFrame.getY() - 417);
        return dialog;
    }

    private JPanel createLoadingPanel(JDialog dialog)
    {
        JPanel loadingPanel = new JPanel(new BorderLayout());
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingPanel.add(progressBar, BorderLayout.CENTER);
        dialog.getContentPane().add(loadingPanel, BorderLayout.NORTH);
        return loadingPanel;
    }

    private void loadIconsAsync(JDialog dialog, JPanel loadingPanel) 
    {
        new Thread(() -> {
            loadIcons();
            SwingUtilities.invokeLater(() -> setupDialogUI(dialog, loadingPanel));
        }).start();
    }

    private void loadIcons() 
    {
        if (cachedImageList.isEmpty())
        {
            File iconsFolder = new File(ICONS_DIRECTORY);
            File[] iconFiles = iconsFolder.listFiles();
            
            if (iconFiles != null) 
            {
                for (File file : iconFiles) 
                {
                    try {
                        ImageIcon icon = new ImageIcon(ImageIO.read(file));
                        cachedImageList.add(new ImageIconFile(icon, file.getName()));
                    } 
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void setupDialogUI(JDialog dialog, JPanel loadingPanel) 
    {
        dialog.remove(loadingPanel);

        DefaultListModel<ImageIconFile> imageListModel = new DefaultListModel<>();
        cachedImageList.forEach(imageListModel::addElement);

        JList<ImageIconFile> imageJList = new JList<>(imageListModel);
        imageJList.setCellRenderer(new ImageIconFileRenderer());
        imageJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel filterPanel = setupFilterPanel(imageListModel);
        JPanel mruPanel = setupMruPanel(dialog);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(imageJList), BorderLayout.CENTER);

        setupImageListListener(imageJList, dialog, mruPanel);

        dialog.getContentPane().add(mruPanel, BorderLayout.EAST);
        dialog.getContentPane().add(mainPanel);
        dialog.revalidate();
        dialog.repaint();
    }

    private JPanel setupFilterPanel(DefaultListModel<ImageIconFile> model)
    {
        JPanel filterPanel = new JPanel();
        JTextField filterField = new JTextField(10);
        filterPanel.add(new JLabel("Filter by Name: "));
        filterPanel.add(filterField);

        filterField.getDocument().addDocumentListener(new DocumentListener() 
        {
            public void insertUpdate(DocumentEvent e) { filterImages(filterField.getText(), model); }
            public void removeUpdate(DocumentEvent e) { filterImages(filterField.getText(), model); }
            public void changedUpdate(DocumentEvent e) { filterImages(filterField.getText(), model); }
        });

        return filterPanel;
    }

    private JPanel setupMruPanel(JDialog dialog)
    {
        JPanel mruPanel = new JPanel();
        mruPanel.setLayout(new BoxLayout(mruPanel, BoxLayout.Y_AXIS));
        updateMruPanel(mruPanel, dialog);
        return mruPanel;
    }

    private void setupImageListListener(JList<ImageIconFile> imageJList, JDialog dialog, JPanel mruPanel) 
    {
        imageJList.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                ImageIconFile selectedImage = imageJList.getSelectedValue();
                spellIcon.setIcon(selectedImage.getIcon());
                updateMruList(selectedImage, mruPanel, dialog);
                dialog.dispose();
            }
        });
    }

    private void updateMruList(ImageIconFile selectedImage, JPanel mruPanel, JDialog parent) 
    {
        if (selectedImage != null) 
        {
            mruImages.remove(selectedImage);
            mruImages.add(0, selectedImage);
            
            if (mruImages.size() > mruLimit) 
            {
                mruImages.remove(mruLimit);
            }
            updateMruPanel(mruPanel, parent);
        }
    }

    private void updateMruPanel(JPanel mruPanel, JDialog parent) 
    {
        mruPanel.removeAll();
        DefaultListModel<ImageIconFile> mruListModel = new DefaultListModel<>();
        mruImages.forEach(mruListModel::addElement);

        JList<ImageIconFile> mruList = new JList<>(mruListModel);
        mruList.setCellRenderer(new ImageIconFileRenderer());
        mruList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mruList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) 
            {
                ImageIconFile selectedIcon = mruList.getSelectedValue();
                
                if (selectedIcon != null)
                {
                    spellIcon.setIcon(selectedIcon.getIcon());
                    parent.dispose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(mruList);
        scrollPane.setPreferredSize(new Dimension(120, 300));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        mruPanel.add(scrollPane);
        mruPanel.revalidate();
        mruPanel.repaint();
    }

    private static void filterImages(String filter, DefaultListModel<ImageIconFile> imageListModel)
    {
        imageListModel.clear();
        
        if (filter.isEmpty()) 
        {
            cachedImageList.forEach(imageListModel::addElement);
        } 
        else 
        {
            cachedImageList.stream()
                .filter(i -> i.getName().toLowerCase().contains(filter.toLowerCase()))
                .forEach(imageListModel::addElement);
        }
    }
    
    private static JLabel createLabel(String text)
    {
        JLabel label = new JLabel(text);
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Main();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
