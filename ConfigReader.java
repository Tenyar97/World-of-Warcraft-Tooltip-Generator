import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigReader 
{
	public static final String MRULIMIT = "mruLimit";
	public static final String DEFAULTSAVE = "defaultPath";
	
    
    public static void updateConfig(String fileName, String key, String newValue) throws IOException 
    {
        File configFile = new File(fileName);
        Map<String, String> configMap = new LinkedHashMap<>();

        if (configFile.exists()) 
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) 
            {
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    if (line.contains("=")) 
                    {
                        String[] parts = line.split("=");
                        if (parts.length == 2) 
                        {
                            configMap.put(parts[0].trim(), parts[1].split(";")[0].trim());
                        }
                    }
                }
            }
        }

        configMap.put(key, newValue);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile)))
        {
            for (Map.Entry<String, String> entry : configMap.entrySet())
            {
                writer.write(entry.getKey() + "=" + entry.getValue() + ";\n");
            }
        }
    }

    public static void readConfig(String fileName) 
    {
        File configFile = new File(fileName);

        try {
            if (!configFile.exists())
            {
            	if (!configFile.exists()) 
            	{
                    try {
                        configFile.createNewFile();
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                        	writer.write(DEFAULTSAVE + "=" + Main.defaultSavePath + "\\Desktop" + ";" + "\n");
                        	writer.write(MRULIMIT + "=20" + ";" + "\n");
                        }
                        System.out.println("Created new config.txt file with default settings");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("config.txt already exists.");
                }
            }

            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) 
            {
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains(";")) 
                    {
                        String[] parts = line.split(";");
                        if (parts.length > 0) 
                        {
                            processConfigLine(parts[0]);
                        }
                    }
                }
            }
        } catch (IOException e)
        { 
            e.printStackTrace();
        }
    }

    private static void processConfigLine(String line) 
    {
        if (line.startsWith("mruLimit="))
        {
            String valueStr = line.substring(MRULIMIT.length() + 1);
            try {
                Main.mruLimit = Integer.parseInt(valueStr);
                System.out.println("Setting MRU to: " + Integer.parseInt(valueStr));
            } catch (NumberFormatException e) 
            {
                System.out.println("Invalid mruLimit value in Config.txt");
            }
        } else if (line.startsWith("defaultPath=")) 
        {
            Main.defaultSavePath = line.substring(DEFAULTSAVE.length());
            System.out.println("Setting default path to: " + line.substring("defaultPath=".length()));
        }
    }
    
}