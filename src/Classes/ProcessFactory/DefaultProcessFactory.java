/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

import Classes.Scheduler.QueueManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultProcessFactory extends ProcessFactory {

    @Override
    public OurProcess createProcess(String name, int numInstructions, boolean isIOBound, int exceptionCycleNumber, int resolutionCycles) {
        try {
            PCB pcb = new PCB(generateUniqueId(), name, numInstructions, isIOBound, exceptionCycleNumber, resolutionCycles);
            
            OurProcess newProcess = new OurProcess(pcb);
            QueueManager queueManager = QueueManager.getInstance();
            queueManager.addNewProcess(newProcess);
            
            
            return newProcess;
        } catch (InterruptedException ex) {
            Logger.getLogger(DefaultProcessFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
