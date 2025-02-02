/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

/**
 *
 * @author Windows 11
 */
public class PCB {

    private String id;
    private String name;
    private int PC;                       // Program Counter
    private int MAR;                      // Memory Address Register
    private ProcessState state;           // READY, RUNNING, BLOCKED
    private Process processRef;
    private int totalInstructions;
    private boolean isIOBound;
    private int exceptionCycleThreshold;  // Cada cuantas instrucciones se lanza una interrupcion (para I/O-bound)
    private int IOResolveCycles;          // Numero de ciclos para resolver la excepción

    public PCB(String id, String name, int totalInstructions, boolean isIOBound, int exceptionCycleThreshold, int IOResolveCycles) {
        this.id = id;
        this.name = name;
        this.PC = 0;
        this.MAR = 0;
        this.state = ProcessState.READY;
        this.processRef = null;
        this.totalInstructions = totalInstructions;
        this.isIOBound = isIOBound;
        this.exceptionCycleThreshold = exceptionCycleThreshold;
        this.IOResolveCycles = IOResolveCycles;
    }

    public void updateState(ProcessState newState) {
        this.setState(newState);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getMAR() {
        return MAR;
    }

    public void setMAR(int MAR) {
        this.MAR = MAR;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public Process getProcessRef() {
        return processRef;
    }

    public void setProcessRef(Process processRef) {
        this.processRef = processRef;
    }

    public int getTotalInstructions() {
        return totalInstructions;
    }

    public void setTotalInstructions(int totalInstructions) {
        this.totalInstructions = totalInstructions;
    }

    public boolean isIOBound() {
        return isIOBound;
    }

    public void setIsIOBound(boolean isIOBound) {
        this.isIOBound = isIOBound;
    }

    public int getExceptionCycleThreshold() {
        return exceptionCycleThreshold;
    }

    public void setExceptionCycleThreshold(int exceptionCycleThreshold) {
        this.exceptionCycleThreshold = exceptionCycleThreshold;
    }

    public int getIOResolveCycles() {
        return IOResolveCycles;
    }

    public void setIOResolveCycles(int IOResolveCycles) {
        this.IOResolveCycles = IOResolveCycles;
    }

}
