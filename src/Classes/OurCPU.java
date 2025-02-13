/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import Classes.ProcessFactory.ProcessState;
import Main.Clock;
import Main.ClockListener;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OurCPU extends Thread {

    private Process currentProcess;
    private final Semaphore tickSemaphore = Clock.getInstance().getTickSemaphore(); // Para esperar cada tick
    private boolean running;
    private boolean isBusy;

    public OurCPU() {
        this.currentProcess = null;
        this.running = true;
        this.isBusy = false;
    }

    public void executeProcess(Process process) {
        if (process == null) {
            System.out.println("CPU intentando ejecutar un proceso nulo.");
            return;
        }

        this.currentProcess = process;
        this.isBusy = true;
        // Actualizamos el estado a RUNNING al iniciar la ejecucion
        process.getPcb().setState(ProcessState.RUNNING);
        System.out.println("OurCPU esta ejecutando el proceso: " + process.getPcb().getName());
    }

    @Override
    public void run() {
        while (running) {
            try {
                tickSemaphore.acquire(); // Esperar el siguiente tick, semaforo de sincronizacion

                if (currentProcess != null) {

                    currentProcess.executeInstruction();

                    // Verificar si el proceso ha terminado
                    if (currentProcess.hasFinished()) {
                        System.out.println("CPU ha terminado el proceso: " + currentProcess.getPcb().getName());
                        //Aqui Manejador de excepciones para eliminar el proceso de ejecucion.
//                        setIsBusy(false);

                    } // Verificar si el proceso está bloqueado (I/O-bound)
                    else if (currentProcess.getPcb().getState() == ProcessState.BLOCKED) {
                        System.out.println("*) CPU detectó que el proceso " + currentProcess.getPcb().getName() + " está bloqueado.");
                        //QueueManager.getInstance().addToBlockedQueue(currentProcess.getPcb());
//                        currentProcess = null;
//                        setIsBusy(false);

                    }

                }
            } catch (InterruptedException e) {
                System.err.println("ERROR CRÍTICO en CPU: " + e.getMessage());
                stopCPU();
            }
        }
    }

    public void stopCPU() {
        this.running = false;
        System.out.println("==================== CPU TERMINATED ====================");
    }

    /**
     * @return the isBusy
     */
    public boolean isIsBusy() {
        return isBusy;
    }

    /**
     * @param isBusy the isBusy to set
     */
    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

}
