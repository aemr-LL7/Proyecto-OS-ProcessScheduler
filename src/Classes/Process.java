/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Interfaces.ClockListener;

/**
 *
 * @author Windows 11
 */
public class Process implements Runnable, ClockListener {

    private int PC;     // Program Counter
    private int MAR;    // Memory Address Register
    private int totalInstructions; //Total de instrucciones
    private boolean isIOBound;     // Caso contrario CPUbound?
    private int excCycleNumber;    // Ciclos para generar exception
    private int IOResolveCycles;   // Numero de ciclos para satisfacer una E/S
    private ProcessState state; // READY, RUNNING, BLOCKED

    public Process(int totalInstructions, boolean isIOBound, int excCycleNumber, int IOResolveCycles) {
        this.totalInstructions = totalInstructions;
        this.isIOBound = isIOBound;
        this.excCycleNumber = excCycleNumber;
        this.IOResolveCycles = IOResolveCycles;
        
        this.state = ProcessState.READY;
        this.PC = 0;
        this.MAR = 0;
        Clock.getInstance().addListenet(this);
    }

    
    
    @Override
    public void run() {
        while (true) {
            if (this.state == ProcessState.RUNNING) {
                onTick(Clock.getInstance().getCurrentCycle());
            }
        }
    }

    @Override
    public void onTick(int currentCycle) {

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

    public int getTotalInstructions() {
        return totalInstructions;
    }

    public void setTotalInstructions(int totalInstructions) {
        this.totalInstructions = totalInstructions;
    }

    public boolean isIsIOBound() {
        return isIOBound;
    }

    public void setIsIOBound(boolean isIOBound) {
        this.isIOBound = isIOBound;
    }

    public int getExcCycleNumber() {
        return excCycleNumber;
    }

    public void setExcCycleNumber(int excCycleNumber) {
        this.excCycleNumber = excCycleNumber;
    }

    public int getIOResolveCycles() {
        return IOResolveCycles;
    }

    public void setIOResolveCycles(int IOResolveCycles) {
        this.IOResolveCycles = IOResolveCycles;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }
    
    
    

}
