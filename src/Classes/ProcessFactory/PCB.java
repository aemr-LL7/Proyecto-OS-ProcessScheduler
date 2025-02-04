/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

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
    private int exceptionSolveNumber;          // Numero de ciclos para resolver la excepción

    public PCB(String id, String name, int totalInstructions, boolean isIOBound, int exceptionCycleThreshold, int exceptionSolveNumber) {
        this.id = id;
        this.name = name;
        this.PC = 0;
        this.MAR = 0;
        this.state = ProcessState.READY;
        this.processRef = null;
        this.totalInstructions = totalInstructions;
        this.isIOBound = isIOBound;
        this.exceptionCycleThreshold = exceptionCycleThreshold;
        this.exceptionSolveNumber = exceptionSolveNumber;
    }

    public void updateState(ProcessState newState) {
        this.setState(newState);
    }
    


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the PC
     */
    public int getPC() {
        return PC;
    }

    /**
     * @param PC the PC to set
     */
    public void setPC(int PC) {
        this.PC = PC;
    }

    /**
     * @return the MAR
     */
    public int getMAR() {
        return MAR;
    }

    /**
     * @param MAR the MAR to set
     */
    public void setMAR(int MAR) {
        this.MAR = MAR;
    }

    /**
     * @return the state
     */
    public ProcessState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ProcessState state) {
        this.state = state;
    }

    /**
     * @return the processRef
     */
    public Process getProcessRef() {
        return processRef;
    }

    /**
     * @param processRef the processRef to set
     */
    public void setProcessRef(Process processRef) {
        this.processRef = processRef;
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
     * @return the isIOBound
     */
    public boolean isIsIOBound() {
        return isIOBound;
    }

    /**
     * @param isIOBound the isIOBound to set
     */
    public void setIsIOBound(boolean isIOBound) {
        this.isIOBound = isIOBound;
    }

    /**
     * @return the exceptionCycleThreshold
     */
    public int getExceptionCycleThreshold() {
        return exceptionCycleThreshold;
    }

    /**
     * @param exceptionCycleThreshold the exceptionCycleThreshold to set
     */
    public void setExceptionCycleThreshold(int exceptionCycleThreshold) {
        this.exceptionCycleThreshold = exceptionCycleThreshold;
    }

    /**
     * @return the exceptionSolveNumber
     */
    public int getExceptionSolveNumber() {
        return exceptionSolveNumber;
    }

    /**
     * @param exceptionSolveNumber the exceptionSolveNumber to set
     */
    public void setExceptionSolveNumber(int exceptionSolveNumber) {
        this.exceptionSolveNumber = exceptionSolveNumber;
    }

    
}
