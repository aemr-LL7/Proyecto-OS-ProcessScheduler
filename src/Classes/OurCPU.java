/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OurCPU extends Thread {

    private Process currentProcess;
    private final Semaphore instructionSemaphore;
    private boolean running;

    public OurCPU(Semaphore instructionSemaphore) {
        this.currentProcess = null;
        this.instructionSemaphore = instructionSemaphore;
        this.running = true;
    }

    public boolean isBusy() {
        return getCurrentProcess() != null;
    }

    public void executeProcess(Process process) {
        this.setCurrentProcess(process);
        System.out.println("OurCPU esta ejecutando el proceso: " + process.getPcb().getName());
        process.start(); // Start execution of process as a thread
    }

    public void terminateCurrentProcess() {
        if (getCurrentProcess() != null) {
            System.out.println("OurCPU termino el proceso: " + getCurrentProcess().getPcb().getName());
            setCurrentProcess(null);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                getInstructionSemaphore().acquire();
                if (getCurrentProcess() != null) {
                    getCurrentProcess().executeInstruction();
                    if (getCurrentProcess().hasFinished()) {
                        terminateCurrentProcess();
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("Critical error in CPU: " + e.getMessage());
                stopCPU(); // Si ocurre un error fatal, stop this CPU
            } finally {
                getInstructionSemaphore().release();
            }
        }
    }

    public void stopCPU() {
        this.setRunning(false);
    }

    /**
     * @return the currentProcess
     */
    public Process getCurrentProcess() {
        return currentProcess;
    }

    /**
     * @param currentProcess the currentProcess to set
     */
    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

    /**
     * @return the instructionSemaphore
     */
    public Semaphore getInstructionSemaphore() {
        return instructionSemaphore;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    
}
