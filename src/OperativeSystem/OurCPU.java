/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.ProcessState;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OurCPU extends Thread {

    private OurProcess currentProcess;
    private final Semaphore tickSemaphore = Clock.getInstance().getTickSemaphore(); // Para esperar cada tick
    private boolean running;
    private boolean isBusy;
    private boolean paused;

    public OurCPU() {
        this.currentProcess = null;
        this.paused = false;
        this.running = true;
        this.isBusy = false;
    }

    public void executeProcess(OurProcess process) {
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
                synchronized (this) {
                    while (paused) {
                        wait();//bloquear hasta que se haga notify
                    }
                }
                
                tickSemaphore.acquire(); // Esperar el siguiente tick, semaforo de sincronizacion
                System.out.println("Estoy procesando");

                if (currentProcess != null) {

                    currentProcess.executeInstruction();

                    // Verificar si el proceso ha terminado
                    if (currentProcess.hasFinished()) {
                        System.out.println("CPU ha terminado el proceso: " + currentProcess.getPcb().getName());

                    } // Verificar si el proceso está bloqueado (I/O-bound)
                    else if (currentProcess.getPcb().getState() == ProcessState.BLOCKED) {
                        System.out.println("CPU detectó que el proceso " + currentProcess.getPcb().getName() + " está bloqueado.");

                    }

                }
            } catch (InterruptedException e) {
                System.err.println("ERROR CRÍTICO en CPU: " + e.getMessage());
                stopCPU();
            }
        }
    }

    public synchronized void pauseCPU() {
        this.paused = true;
    }

    public synchronized void resumeCPU() {
        this.paused = false;
        notify();
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

    public OurProcess getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(OurProcess currentProcess) {
        this.currentProcess = currentProcess;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}
