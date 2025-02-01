/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

/**
 *
 * @author Windows 11
 */
public class Process {

    private PCB pcb;
    private int totalInstructions;
    private boolean isIOBound;     // Caso contrario CPUbound?
    private int excCycleNumber;     // Ciclos para generar exception
    private int IOResolveCycles;       // Numero de ciclos para satisfacer una exception

    public Process(PCB pcb) {
        this.pcb = pcb;
    }

    /**
     * @return the pcb
     */
    public PCB getPcb() {
        return pcb;
    }

    /**
     * @return the totalInstructions
     */
    public int getTotalInstructions() {
        return totalInstructions;
    }

    /**
     * @param totalInstructions the totalInstructions to set
     */
    public void setTotalInstructions(int totalInstructions) {
        this.totalInstructions = totalInstructions;
    }

    public boolean isIsIOBound() {
        return isIOBound;
    }

    public void setIsIOBound(boolean isIOBound) {
        this.isIOBound = isIOBound;
    }

    public int getIOResolveCycles() {
        return IOResolveCycles;
    }

    public void setIOResolveCycles(int IOResolveCycles) {
        this.IOResolveCycles = IOResolveCycles;
    }

    
    
    /**
     * @return the excCycleNumber
     */
    public int getExcCycleNumber() {
        return excCycleNumber;
    }

    /**
     * @param excCycleNumber the excCycleNumber to set
     */
    public void setExcCycleNumber(int excCycleNumber) {
        this.excCycleNumber = excCycleNumber;
    }

}
