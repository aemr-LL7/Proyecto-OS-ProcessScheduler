/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

/**
 *
 * @author Windows 11
 */
public class SimulationConfig {
    private static SimulationConfig simuInstance;

    private int cycleDuration;      // Duración cada ciclo en ms
    private int totalCycles;
    private int numCPU;
    private int cycleQty;  // Cantidad de instrucciones que se ejecutan por ciclo

    private SimulationConfig() {
        // Valores por defecto
        this.cycleDuration = 1000;      // 1000 ms = 1 segundo
        this.totalCycles = 100;        // 100 ciclos
        this.numCPU = 2;
        this.cycleQty = 1;    // 1 instruccion por ciclo
    }

    public static synchronized SimulationConfig getInstance() {
        if (getSimuInstance() == null) {
            setSimuInstance(new SimulationConfig());
        }
        return getSimuInstance();
    }

    /**
     * @return the simuInstance
     */
    public static SimulationConfig getSimuInstance() {
        return simuInstance;
    }

    /**
     * @param aSimuInstance the simuInstance to set
     */
    public static void setSimuInstance(SimulationConfig aSimuInstance) {
        simuInstance = aSimuInstance;
    }

    /**
     * @return the cycleDuration
     */
    public int getCycleDuration() {
        return cycleDuration;
    }

    /**
     * @param cycleDuration the cycleDuration to set
     */
    public void setCycleDuration(int cycleDuration) {
        this.cycleDuration = cycleDuration;
    }

    /**
     * @return the totalCycles
     */
    public int getTotalCycles() {
        return totalCycles;
    }

    /**
     * @param totalCycles the totalCycles to set
     */
    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }

    /**
     * @return the numCPU
     */
    public int getNumCPU() {
        return numCPU;
    }

    /**
     * @param numCPU the numCPU to set
     */
    public void setNumCPU(int numCPU) {
        this.numCPU = numCPU;
    }

    /**
     * @return the cycleQty
     */
    public int getCycleQty() {
        return cycleQty;
    }

    /**
     * @param cycleQty the cycleQty to set
     */
    public void setCycleQty(int cycleQty) {
        this.cycleQty = cycleQty;
    }
    
    
}

