/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.Process;
import EDD.OurQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows 11 ###### Re-estructurar
 */
public class FirstComeFirstServed implements Scheduler {

    private QueueManager queueManager = QueueManager.getInstance();

    @Override
    public Process getNextProcess() {
        Process process = null;
        PCB nextProcessPCB = queueManager.getNextReadyProcess();
        try {
            process = queueManager.getProcessByPCB(nextProcessPCB);
            return process;
        } catch (InterruptedException ex) {
            
        }
        
        return process;
    }
}
