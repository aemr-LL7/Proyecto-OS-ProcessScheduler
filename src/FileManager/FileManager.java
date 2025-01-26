/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

/**
 *
 * @author andre
 */
public class FileManager {

    private String SAVED_DIRECTORY = ".saved";
    private File dataFile;

    public FileManager() {

    }

    private void writeInitialStructure() {
        String initialContent = "[General Params]\n"
                + "Duration=1000\n"
                + "Deadline=10\n\n"
                + "[HP]\n"
                + "Motherboard=1\n"
                + "CPU=1\n"
                + "RAM=1\n"
                + "PSU=1\n"
                + "GPU=1\n"
                + "Assemblers=1\n"
                + "MaxCapacity=25\n\n"
                + "[MSI]\n"
                + "Motherboard=1\n"
                + "CPU=1\n"
                + "RAM=1\n"
                + "PSU=1\n"
                + "GPU=1\n"
                + "Assemblers=1\n"
                + "MaxCapacity=25\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write(initialContent);
            System.out.println("Estructura inicial escrita en el archivo data.txt");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al escribir en el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void savedDirectoryExists() {
        try {
            Path savedDirectoryPath = Paths.get(this.SAVED_DIRECTORY);

            // Si la carpeta .saved no existe, la creamos
            if (!Files.exists(savedDirectoryPath)) {
                Files.createDirectory(savedDirectoryPath);
                System.out.println("Carpeta .saved creada en el directorio raiz");
            }

            // Crear el archivo data.txt dentro de la carpeta .saved si no existe
            this.dataFile = new File(savedDirectoryPath.toFile(), "data.txt");
            if (!dataFile.exists()) {
                boolean fileCreated = dataFile.createNewFile();
                if (fileCreated) {
                    System.out.println("Archivo data.txt creado dentro de la carpeta .saved");
                    this.writeInitialStructure(); // Escribir la estructura inicial en el archivo
                } else {
                    System.out.println("Error al crear el archivo data.txt");
                }
            } else {
                System.out.println("El archivo data.txt ya existe!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al crear la carpeta o el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public void loadParamsFromTxt() {
//        String fileWithData = this.readFile(App.getSelectedFile());
//        System.out.println(fileWithData);
//        int[] params = this.getGeneralParams(fileWithData);
//        if (params != null && params.length >= 2) {
//            App.setDayDuration(params[0]);
//            App.setDeadline(params[1]);
//        }
//        // Añadir los parametros de las compañias con APP
//        App.setFactoryHP(this.createFactory(0));
//        App.setFactoryMSI(this.createFactory(1));
//    }

//    public void writeData(File inFile) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inFile))) {
//            // Escribir la seccion de parametros generales
//            writer.write("[General Params]\n");
//            writer.write("Duration=" + App.getDayDuration() + "\n");
//            writer.write("Deadline=" + App.getDeadline() + "\n\n");
//
//            // Escribir la sección de HP
////            Company hp = Home.getCompany0();
//            writer.write("[HP]\n");
//            writer.write("Motherboard=" + 1 + "\n");
//            writer.write("CPU=" + 1 + "\n");
//            writer.write("RAM=" + 1 + "\n");
//            writer.write("PSU=" + 1 + "\n");
//            writer.write("GPU=" + 1 + "\n");
//            writer.write("Assemblers=" + 1 + "\n");
//            writer.write("MaxCapacity=" + 25 + "\n\n");
//
//            // Escribir la sección de MSI
////            Company msi = Home.getCompany1();
//            writer.write("[MSI]\n");
//            writer.write("Motherboard=" + 1 + "\n");
//            writer.write("CPU=" + 1 + "\n");
//            writer.write("RAM=" + 1 + "\n");
//            writer.write("PSU=" + 1 + "\n");
//            writer.write("GPU=" + 1 + "\n");
//            writer.write("Assemblers=" + 1 + "\n");
//            writer.write("MaxCapacity=" + 25 + "\n");
//
//            System.out.println("==================== Datos escritos correctamente en el archivo");
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error al escribir los datos en el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private String readFile(File file) {
        String line;
        String data = "";

        try {
            if (!file.exists()) {
                file.createNewFile();

            } else {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                while ((line = br.readLine()) != null) {
                    if (!(line.isEmpty())) {
                        data += line + "\n";
                    }
                }
                br.close();
            }
            return data;
        } catch (Exception e) {
        }
        return data;
    }

    private int[] getGeneralParams(String params) {
        int[] generalParams = new int[2];
        boolean inGeneralParams = false;
        boolean foundDuration = false;
        boolean foundDeadline = false;

        String[] lines = params.split("\n");

        for (String line : lines) {
            line = line.trim();

            // Si encontramos la etiqueta [General Params]
            if (line.equals("[General Params]")) {
                inGeneralParams = true;
            } else if (line.startsWith("[") && inGeneralParams) {
                // Si encontramos otra sección después de [General Params], salimos
                break;
            } else if (inGeneralParams) {
                if (line.startsWith("Duration=")) {
                    String durationValue = line.split("=")[1].trim();
                    if (!durationValue.isEmpty()) {
                        generalParams[0] = Integer.parseInt(durationValue);
                        foundDuration = true;
                    }
                } else if (line.startsWith("Deadline=")) {
                    String deadlineValue = line.split("=")[1].trim();
                    if (!deadlineValue.isEmpty()) {
                        generalParams[1] = Integer.parseInt(deadlineValue); // Extraemos y guardamos el valor de Deadline
                        foundDeadline = true; // Marcamos que se encontró un valor valido para Deadline
                    }
                }
            }
        }

        // Verificamos si encontramos [General Params]
        if (!inGeneralParams) {
            JOptionPane.showMessageDialog(null, "No se encontró la sección [General Params] en el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Si no se encontraron valores validos, mostramos un mensaje de error
            if (!foundDuration || !foundDeadline) {
                String errorMessage = "Los parámetros no son válidos.";
                if (!foundDuration) {
                    errorMessage += "\nFaltante o inválido: Duration=";
                }
                if (!foundDeadline) {
                    errorMessage += "\nFaltante o inválido: Deadline=";
                }
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return generalParams;
    }

//    // Para HP index = 0 y MSI = 1
//    private Factory createFactory(int company) {
//        // Se obtiene los datos del TXT
//        int[] companyValues = this.getCompanyValues(company);
//
//        int standardPriceByCompany;
//        int gpuPriceByCompany;
//        if (company == 0) {
//            standardPriceByCompany = 90000; // HP
//            gpuPriceByCompany = 140000;
//        } else {
//            standardPriceByCompany = 180000; // MSI  
//            gpuPriceByCompany = 250000;
//        }
//
//        // Verificar que se hayan obtenido los valores correctamente
//        if (companyValues.length != 7) {
//            JOptionPane.showMessageDialog(null, "No se pudieron obtener todos los valores de la compañía.", "Error", JOptionPane.ERROR_MESSAGE);
//            return null;
//        }
//
////        // Crear el almacen correspondiente
//        //Warehouse warehouseInstance = new Warehouse(company == 0 ? "HP Warehouse" : "MSI Warehouse");
//        Company companyInstance = new Company(company == 0 ? "HP" : "MSI", standardPriceByCompany, gpuPriceByCompany);
//        ProjectManager PM = new ProjectManager(companyInstance, App.getDayDuration(), App.getDeadline());
//        Director DIRECTOR = new Director(PM, companyInstance, companyInstance.getInventoryWareHouse());
//        
//        Factory factory = new Factory(
//                companyInstance, // Instancia de la compañía
//                App.getDayDuration(), // Duracion del día en ms (por ejemplo, 1000 ms)
//                App.getDeadline(), // Dias para la entrega final
//                companyValues[0], // Motherboard workers
//                companyValues[1], // CPU workers
//                companyValues[2], // RAM workers
//                companyValues[3], // PSU workers
//                companyValues[4], // GPU workers
//                companyValues[5] // Assembly workers
//        );
//        factory.setMaxWorkersQty(companyValues[6]);
//        factory.setPM(PM);
//        factory.setDirector(DIRECTOR);
//
////        System.out.println(warehouseInstance.getCompany());
////        factory.getWorkersCountByType();
////        System.out.println("Componentes producidos de la warehouse:");
////        System.out.println("MOBOS:" + warehouseInstance.getMOBO_Count() + "\nCPU:" + warehouseInstance.getCPU_Count() + "\nRAM:" + warehouseInstance.getRAM_Count() + "\nPSU:" + warehouseInstance.getPSU_Count() + "\nGPU:" + warehouseInstance.getGPU_Count());
////        // Devolver la instancia de la compañía
//        return factory;
//    }

//    // Buscar en data.txt los parámetros de las etiquetas [HP] y [MSI]
//    public int[] getCompanyValues(int companyIndex) {
//        // Cambia el tamaño del array a 7 para incluir todos los parámetros
//        int[] companyParams = new int[7];
//
//        // Determinamos la etiqueta de la compañía según el índice
//        String companyTag = companyIndex == 0 ? "HP" : "MSI";
//
//        // Leer el archivo
//        String fileData = readFile(App.getSelectedFile());
//        String[] lines = fileData.split("\n");
//
//        boolean inCompanySection = false;
//
//        for (String line : lines) {
//            line = line.trim();
//
//            if (line.equals("[" + companyTag + "]")) {
//                inCompanySection = true;
//                continue; // Continuamos a la siguiente línea
//            }
//
//            if (line.startsWith("[") && inCompanySection) {
//                // Si encontramos otro tag después de la compañía, salimos
//                break;
//            }
//
//            if (inCompanySection) {
//                if (line.startsWith("Motherboard=")) {
//                    companyParams[0] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("CPU=")) {
//                    companyParams[1] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("RAM=")) {
//                    companyParams[2] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("PSU=")) {
//                    companyParams[3] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("GPU=")) {
//                    companyParams[4] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("Assemblers=")) {
//                    companyParams[5] = Integer.parseInt(line.split("=")[1].trim());
//                } else if (line.startsWith("MaxCapacity=")) {
//                    companyParams[6] = Integer.parseInt(line.split("=")[1].trim());
//                }
//            }
//        }
//
//        // Validar los parámetros encontrados
//        if (!inCompanySection) {
//            JOptionPane.showMessageDialog(null, "No se encontró la sección [" + companyTag + "] en el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//
//        return companyParams;
//    }

//    private void printLoadedParams() {
//        System.out.println("Duración del día: " + dayDuration);
//        System.out.println("Fecha límite: " + deadline);
//        System.out.println("Capacidades de almacenes:");
//        System.out.println("Motherboard Warehouse: " + warehouseCapacities[0]);
//        System.out.println("CPU Warehouse: " + warehouseCapacities[1]);
//        System.out.println("RAM Warehouse: " + warehouseCapacities[2]);
//        System.out.println("PowerSupply Warehouse: " + warehouseCapacities[3]);
//        System.out.println("GPU Warehouse: " + warehouseCapacities[4]);
//    }
}
