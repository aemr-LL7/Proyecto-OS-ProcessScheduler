/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.ProcessState;
import Classes.Scheduler.QueueManager;
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
            // Si el proceso interrumpido es el que se está ejecutando en este CPU
            if (cpu.getCurrentProcess() != null && cpu.getCurrentProcess().equals(process)) {

                cpu.pauseCPU(); // Pausar la ejecución del CPU
                QueueManager.getInstance().addToBlockedQueue(process.getPcb());
                cpu.setCurrentProcess(null); // Limpiar el proceso actual
                cpu.setIsBusy(false);
                cpu.resumeCPU(); // Reanudar el CPU para que ejecute otros procesos

                System.out.println("[EH] Proceso " + process.getPcb().getName() + " interrumpido y movido a bloqueados");

                // Agregar listener al Clock para resolver la espera de I/O ????

                break;
            }
            auxNode = auxNode.getpNext();
        }
    }



}
