/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

public class DefaultProcessFactory extends ProcessFactory {

    @Override
    public Process createProcess(String name, int numInstructions, boolean isIOBound, int exceptionCycleNumber, int resolutionCycles) {
        PCB pcb = new PCB(generateUniqueId(), name, numInstructions, isIOBound, exceptionCycleNumber, resolutionCycles);
        
        Process newProcess = new Process(pcb);
        pcb.setProcessRef(newProcess);
        return newProcess;
    }
}
