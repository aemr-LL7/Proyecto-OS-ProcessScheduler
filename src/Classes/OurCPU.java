/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

/**
 *
 * @author Windows 11
 */
public class OurCPU {

    private Process currentProcess;
    private boolean isBusy;
    
    public OurCPU() {
        this.currentProcess = null;
        this.isBusy = false;
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
     * @return the isBusy
     */
    public boolean isBusy() {
        return isBusy;
    }
    
    
}
