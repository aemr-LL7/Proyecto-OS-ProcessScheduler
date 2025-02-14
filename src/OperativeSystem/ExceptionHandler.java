/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import EDD.SimpleList;
import EDD.SimpleNode;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11 => Clase para manejar las interrupciones generadas por
 * procesos consumidores de operaciones i/o
 */
public class ExceptionHandler {

    // Instancia única de la clase
    private static ExceptionHandler instance;
    private final Semaphore mutex = new Semaphore(1);

    // Constructor privado para evitar instanciación externa
    private ExceptionHandler() {
    }

    // Método estático para obtener la instancia única
    public static synchronized ExceptionHandler getInstance() {
        if (instance == null) {
            instance = new ExceptionHandler();
        }
        return instance;
    }

    public void IOInterruption(OurProcess process) {
        SimpleList<OurCPU> cpuList = OperatingSystem.getInstance().getCpuList();
        SimpleNode<OurCPU> auxNode = cpuList.getpFirst();

        while (auxNode != null) {
            OurCPU cpu = auxNode.getData();
            if (cpu.getCurrentProcess() != null && cpu.getCurrentProcess().equals(process)) {
                cpu.pauseCPU();           // Pausar la ejecución del CPU
                cpu.setCurrentProcess(null);  // Limpiar el proceso actual
                cpu.resumeCPU();         //reanudar
                System.out.println("Te interrumpi mmg: " + process.getPcb().getName());
                break; 
            }
            auxNode = auxNode.getpNext();
        }
    }

}
