/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import OperativeSystem.QueueManager;
import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.OurProcess;
import OperativeSystem.OurCPU;
/**
 *
 * @author Windows 11 ###### Re-estructurar
 */
public class pFirstComeFirstServed implements Scheduler {

    public pFirstComeFirstServed() {
    }

    @Override
    public OurProcess getNextProcess() {
        PCB nextProcessPCB = QueueManager.getInstance().getNextReadyProcess();

        if (nextProcessPCB == null) {
            System.out.println("[FCFS]\nNo hay procesos en la cola de listos");
            return null;
        }

        try {
            OurProcess process = QueueManager.getInstance().getProcessByPCB(nextProcessPCB);
            return process;
        } catch (InterruptedException ex) {
            System.out.println("Error al obtener el proceso desde la PCB.");
            return null;
        }
    }

    @Override
    public void checkFlags(OurCPU cpu) {
        
    }
}
