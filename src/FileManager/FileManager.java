/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import EDD.OurHashTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

/**
 *
 * @author andre
 */
public class FileManager {

    private final String SAVED_DIRECTORY = ".saved";
    private File configFile;
    private File processDataFile;
    private final Gson gson;

    public FileManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.initializeDirectory();
    }

    private void initializeDirectory() {
        try {
            Path savedDirectoryPath = Paths.get(this.SAVED_DIRECTORY);

            if (!Files.exists(savedDirectoryPath)) {
                Files.createDirectory(savedDirectoryPath);
                System.out.println("Carpeta .saved creada en el directorio raiz");
            }

            this.configFile = new File(savedDirectoryPath.toFile(), "simulation_config.txt");
            this.processDataFile = new File(savedDirectoryPath.toFile(), "process_data.json");

            if (!configFile.exists()) {
                configFile.createNewFile();
                System.out.println("Archivo simulation_config.txt creado");
            }

            if (!processDataFile.exists()) {
                processDataFile.createNewFile();
                System.out.println("Archivo process_data.json creado");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al crear la carpeta o archivos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveSimulationConfig(int cycleDuration, int numberOfCpus, int numberOfProcesses) {
        String configContent = "[General Params]\n"
                + "CycleDuration=" + cycleDuration + "\n"
                + "NumberOfCpus=" + numberOfCpus + "\n"
                + "NumberOfProcesses=" + numberOfProcesses + "\n\n"
                + "[CPUS]\n";

        for (int i = 0; i < numberOfCpus; i++) {
            configContent += "cpuId=" + i + "\n";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(configContent);
            System.out.println("Configuración guardada en " + configFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar la configuración.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveProcessData(OurHashTable<OurProcess> pcbTable) {
        try (
                Writer writer = new FileWriter(processDataFile)) {
            // Convertir la HashTable a una SimpleList de PCBData

            // Convertir a JSON y escribir al archivo
            // gson.toJson(container, writer);
            System.out.println("Datos de PCBs guardados en " + processDataFile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar los datos de PCBs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFile(File file) {
        StringBuilder data = new StringBuilder();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return data.toString();
    }

    private int[] getGeneralParams(String params) {
        int[] generalParams = new int[3];
        boolean inGeneralParams = false;

        String[] lines = params.split("\n");
        for (String line : lines) {
            line = line.trim();

            if (line.equals("[General Params]")) {
                inGeneralParams = true;
            } else if (line.startsWith("[") && inGeneralParams) {
                break;
            } else if (inGeneralParams) {
                if (line.startsWith("CycleDuration=")) {
                    generalParams[0] = Integer.parseInt(line.split("=")[1].trim());
                } else if (line.startsWith("NumberOfCpus=")) {
                    generalParams[1] = Integer.parseInt(line.split("=")[1].trim());
                } else if (line.startsWith("NumberOfProcesses=")) {
                    generalParams[2] = Integer.parseInt(line.split("=")[1].trim());
                }
            }
        }

        return generalParams;
    }
}
