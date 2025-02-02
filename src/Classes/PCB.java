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

    private int id;
    private String name;
    private int PC;     // Program Counter
    private int MAR;      // Memory Address Register
    private ProcessState state; // READY, RUNNING, BLOCKED
    private Process processRef;

    public PCB(int id, String name) {
        this.id = id;
        this.name = name;
        this.PC = 0;
        this.MAR = 0;
        this.state = ProcessState.READY;
        this.processRef = null;
    }

    public void updateState(ProcessState newState) {
        this.setState(newState);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
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

    public Process getProcessRef() {
        return processRef;
    }

    public void setProcessRef(Process processRef) {
        this.processRef = processRef;
    }

}
