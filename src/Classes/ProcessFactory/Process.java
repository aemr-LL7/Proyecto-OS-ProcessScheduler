/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

import Main.SimulationConfig;

/**
 *
 * @author Windows 11
 */
public class Process extends Thread {

    private PCB pcb;
    private int executedInstructions;

    public Process(PCB pcb) {
        this.pcb = pcb;
        this.executedInstructions = 0;
    }

    public Boolean isBlocked() {
        return this.pcb.getState() == ProcessState.BLOCKED;
    }

    public void executeInstruction() {
        if (this.pcb.getState() == ProcessState.BLOCKED) {
            System.out.println("El proceso se encuentra bloqueado!");
        }
        this.executedInstructions++;
        pcb.setPC(pcb.getPC() + 1);
        pcb.setMAR(pcb.getMAR() + 1);
    }

    public Boolean hasFinished() {
        return executedInstructions >= pcb.getTotalInstructions();
    }

    @Override
    public void run() {
        SimulationConfig simuConfig = SimulationConfig.getInstance();
        while (this.executedInstructions < this.getTotalInstructions()) {
            if (this.pcb.getState() != ProcessState.BLOCKED) {

                int instructionsThisCycle = simuConfig.getCycleQty();   // Cuantas instrucciones se ejecutaran en este ciclo

                for (int i = 0; i < instructionsThisCycle && this.executedInstructions < this.getTotalInstructions(); i++) {

                    this.executeInstruction();

                    // Verificar si se debe lanzar una interrpu en procesos I/O-bound
                    if (this.isIOBound() && (this.executedInstructions % this.getExceptionCycleThreshold() == 0)) {
                        System.out.println("Interrupcion lanzada del proceso: " + pcb.getName());
                        // Se "detiene" el proceso durante los ciclos de resolucion
                        try {
                            Thread.sleep(this.pcb.getExceptionSolveNumber() * simuConfig.getCycleDuration());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Simula la duración del ciclo
                try {
                    Thread.sleep(simuConfig.getCycleDuration());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Proceso " + pcb.getName() + " ha completado su ejecucion.");
        }

    }

    /**
     * @return the pcb
     */
    public PCB getPcb() {
        return pcb;
    }

    /**
     * @param pcb the pcb to set
     */
    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    /**
     * @return the totalInstructions
     */
    public int getTotalInstructions() {
        return this.pcb.getTotalInstructions();
    }

    /**
     * @param totalInstructions the totalInstructions to set
     */
    public void setTotalInstructions(int totalInstructions) {
        this.pcb.setTotalInstructions(totalInstructions);
    }

    /**
     * @return the isIOBound
     */
    public boolean isIOBound() {
        return this.pcb.isIsIOBound();
    }

    /**
     * @param isIOBound the isIOBound to set
     */
    public void setIsIOBound(boolean isIOBound) {
        this.pcb.setIsIOBound(isIOBound);
    }

    /**
     * @return the exceptionCycleThreshold
     */
    public int getExceptionCycleThreshold() {
        return this.pcb.getExceptionCycleThreshold();
    }

    /**
     * @param exceptionCycleThreshold the exceptionCycleThreshold to set
     */
    public void setExceptionCycleThreshold(int exceptionCycleThreshold) {
        this.pcb.setExceptionCycleThreshold(exceptionCycleThreshold);
    }

    /**
     * @return the IOResolveCycles
     */
    public int getExceptionSolveNumber() {
        return this.pcb.getExceptionSolveNumber();
    }

    /**
     * @param IOResolveCycles the IOResolveCycles to set
     */
    public void setExceptionSolveNumber(int IOResolveCycles) {
        this.pcb.setExceptionSolveNumber(IOResolveCycles);
    }

    /**
     * @return the executedInstructions
     */
    public int getExecutedInstructions() {
        return executedInstructions;
    }

    /**
     * @param executedInstructions the executedInstructions to set
     */
    public void setExecutedInstructions(int executedInstructions) {
        this.executedInstructions = executedInstructions;
    }

    public String getPid() {
        return this.pcb.getId();
    }

}
