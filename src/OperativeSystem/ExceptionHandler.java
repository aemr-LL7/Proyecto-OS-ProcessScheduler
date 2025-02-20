/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.ProcessState;
import EDD.SimpleList;
import EDD.SimpleNode;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public void interruptCPU(OurProcess process) {
        try {
            this.mutex.acquire(); // Adquirir el semáforo antes de acceder a la sección crítica

            SimpleList<OurCPU> cpuList = OperatingSystem.getInstance().getCpuList();
            SimpleNode<OurCPU> auxNode = cpuList.getpFirst();

            while (auxNode != null) {
                OurCPU cpu = auxNode.getData();
                // Si el proceso interrumpido es el que se está ejecutando en este CPU
                if (cpu.getCurrentProcess() != null && cpu.getCurrentProcess().equals(process)) {
                    System.out.println("[EH] Proceso " + process.getPcb().getName() + " interrumpido");
                    cpu.signalInterrupt();
                    break;
                    
                }
                auxNode = auxNode.getpNext();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
        } finally {
            this.mutex.release(); // Liberar el semáforo después de acceder a la sección crítica
        }
    }

    //Excepcion 
    public void interruptCPU(OurCPU cpu) {
        
        try {
            this.mutex.acquire();
            
            OurProcess process = cpu.getCurrentProcess();
            QueueManager.getInstance().addToBlockedQueue(process.getPcb());
            cpu.setCurrentProcess(null); // Limpiar el proceso actual
            cpu.setIsBusy(false);
            cpu.clearInterrupt();
            
            
            System.out.println("[EH] Proceso " + process.getPcb().getName() + " interrumpido y movido a bloqueados");
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(ExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.mutex.release();
        }
    }

    public Semaphore getMutex() {
        return mutex;
    }

}
