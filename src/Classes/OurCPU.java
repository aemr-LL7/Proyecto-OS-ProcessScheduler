/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import Classes.Scheduler.QueueManager;
import Main.Clock;
import Main.ClockListener;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OurCPU extends Thread implements ClockListener {

    private Process currentProcess;
    private final Semaphore instructionSemaphore; //Con este semaforo aqui no vamos a poder tener concurrencia 
    private final Semaphore tickSemaphore; // Para esperar cada tick
    private boolean running;

    public OurCPU(Semaphore instructionSemaphore, Semaphore tickSemaphore) {
        this.currentProcess = null;
        this.instructionSemaphore = instructionSemaphore;
        this.tickSemaphore = tickSemaphore;
        this.running = true;
    }

    public boolean isBusy() {
        return currentProcess != null;
    }

    public void executeProcess(Process process) {
        this.currentProcess = process;
        System.out.println("OurCPU está ejecutando el proceso: " + process.getPcb().getName());
    }

    public void terminateCurrentProcess() {
        if (currentProcess != null) {
            System.out.println("OurCPU ha terminado el proceso: " + currentProcess.getPcb().getName());
            currentProcess = null;
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                tickSemaphore.acquire(); // Esperar el siguiente tick

                if (currentProcess != null) {
                    instructionSemaphore.acquire(); // Bloquear la ejecución de instrucciones
                    currentProcess.executeInstruction();

                    if (currentProcess.hasFinished()) {
                        terminateCurrentProcess();
                    } else {
                        QueueManager.getInstance().addToReadyQueue(currentProcess);
                    }

                    instructionSemaphore.release(); // Liberar el semáforo
                }
            } catch (InterruptedException e) {
                System.err.println("Error crítico en CPU: " + e.getMessage());
                stopCPU();
            }
        }
    }

    // Este método se podría usar para que el OS o el Clock libere el tickSemaphore:
    @Override
    public void onTick(int currentCycle) {
        // Simplemente liberar un tick para este CPU (si no se hace desde el OS)
        tickSemaphore.release();
    }

    public void stopCPU() {
        this.running = false;
    }
}
