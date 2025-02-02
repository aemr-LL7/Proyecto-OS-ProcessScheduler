/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import Main.SimulationConfig;

/**
 *
 * @author Windows 11
 */
public class OurCPU {

    private Process currentProcess;
    private boolean busy;

    public OurCPU() {
        this.currentProcess = null;
        this.busy = false;
    }

    public void executeProcess(Process process) {
        this.currentProcess = process;
        this.busy = true;
        System.out.println("OurCPU está ejecutando el proceso: " + process.getPcb().getName());

        // In this simulation, the process thread might already be running.
        // However, to simulate the CPU execution, we can start a new thread that
        // waits for the process to complete its cycle.
        new Thread(() -> {
            try {
                SimulationConfig config = SimulationConfig.getInstance();
                // Simulate execution time based on the total instructions and cycle duration.
                Thread.sleep(process.getTotalInstructions() * config.getCycleDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.terminateCurrentProcess();
        }).start();
    }

    public void terminateCurrentProcess() {
        if (currentProcess != null) {
            System.out.println("OurCPU finalizo la ejecucion del proceso: " + currentProcess.getPcb().getName());
            currentProcess = null;
            busy = false;
        }
    }

    public boolean hasException() {
        // Simula una prob de 10% de tener una excepcion en cada ciclo
        return currentProcess != null && Math.random() < 0.1;
    }

    public boolean isCPUBusy() {
        return isBusy();
    }

    /**
     * @return the currentProcess
     */
    public Process getCurrentProcess() {
        return currentProcess;
    }

    /**
     * @return the busy
     */
    public boolean isBusy() {
        return busy;
    }

}
