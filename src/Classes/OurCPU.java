/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import Classes.ProcessFactory.ProcessState;
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

    private int quantumCounter;  // Contador de instrucciones ejecutadas en el quantum
    private final int quantum;   // Quantum asignado

    public OurCPU(Semaphore instructionSemaphore, Semaphore tickSemaphore, int quantum) {
        this.currentProcess = null;
        this.instructionSemaphore = instructionSemaphore;
        this.tickSemaphore = tickSemaphore;
        this.running = true;
        this.quantumCounter = 0;
        this.quantum = quantum;
    }

    public boolean isBusy() {
        return currentProcess != null;
    }

    public void executeProcess(Process process) {
        this.currentProcess = process;
        this.quantumCounter = 0;
        // Actualizamos el estado a RUNNING al iniciar la ejecucion
        process.getPcb().setState(ProcessState.RUNNING);
        System.out.println("OurCPU esta ejecutando el proceso: " + process.getPcb().getName());
    }

    public void terminateCurrentProcess() {
        if (currentProcess != null) {
            System.out.println("OurCPU ha terminado el proceso: " + currentProcess.getPcb().getName());
            QueueManager.getInstance().addToFinishedProcessesList(currentProcess.getPcb()); // Mover a terminados
            currentProcess = null; // Liberar CPU
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
                        System.out.println("CPU ha terminado el proceso: " + currentProcess.getPcb().getName());
                        QueueManager.getInstance().addToFinishedProcessesList(currentProcess.getPcb());
                        currentProcess = null; // ⚠️ LIBERAR EL CPU
                    } else if (currentProcess.getPcb().getState() == ProcessState.BLOCKED) {
                        System.out.println("CPU detectó que el proceso " + currentProcess.getPcb().getName() + " está bloqueado.");
                        QueueManager.getInstance().addToBlockedQueue(currentProcess.getPcb());
                        currentProcess = null; // ⚠️ LIBERAR EL CPU
                    } else {
                        QueueManager.getInstance().addToReadyQueue(currentProcess.getPcb());
                        currentProcess = null; // ⚠️ LIBERAR EL CPU
                    }

                    
                    instructionSemaphore.release(); // Liberar el semaforo
                }
            } catch (InterruptedException e) {
                System.err.println("Error crítico en CPU: " + e.getMessage());
                stopCPU();
            }
        }
    }

    @Override
    public void onTick(int currentCycle) {
        System.out.println("OurCPU recibe tick: ciclo " + currentCycle);
        tickSemaphore.release();  // Liberar el semáforo para que el CPU pueda avanzar
    }

    public void stopCPU() {
        this.running = false;
        System.out.println("==================== CPU TERMINATED ====================");
    }
}
