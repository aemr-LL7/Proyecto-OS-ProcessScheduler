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
    private boolean isCpuBound;     // Caso contrario i/o bound?
    private int excCycleNumber;     // Ciclos para generar exception
    private int excResolveCycles;       // Numero de ciclos para satisfacer una exception

    public Process(PCB pcb, int totalInstructions, boolean isCpuBound, int exceptionCycleNumber, int exceptionResolveCycles) {
        this.pcb = pcb;
        this.totalInstructions = totalInstructions;
        this.isCpuBound = isCpuBound;
        this.excCycleNumber = exceptionCycleNumber;
        this.excResolveCycles = exceptionResolveCycles;
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

    /**
     * @return the isCpuBound
     */
    public boolean isIsCpuBound() {
        return isCpuBound;
    }

    /**
     * @param isCpuBound the isCpuBound to set
     */
    public void setIsCpuBound(boolean isCpuBound) {
        this.isCpuBound = isCpuBound;
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

    /**
     * @return the excResolveCycles
     */
    public int getExcResolveCycles() {
        return excResolveCycles;
    }

    /**
     * @param excResolveCycles the excResolveCycles to set
     */
    public void setExcResolveCycles(int excResolveCycles) {
        this.excResolveCycles = excResolveCycles;
    }
    
    
}
