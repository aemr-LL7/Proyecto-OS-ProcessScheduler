/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.Process;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;

/**
 *
 * @author Windows 11
 */
public class OperatingSystem {

    private final QueueManager queueManager;
    private final SimpleList<OurCPU> cpuList;
    private final Scheduler scheduler;

    public OperatingSystem(QueueManager queueManager, SimpleList<OurCPU> CPU_List, Scheduler scheduler) {
        this.queueManager = queueManager;
        this.cpuList = CPU_List;
        this.scheduler = scheduler;
    }

    public void scheduleProcesses() {
        for (int i = 0; i < cpuList.getSize(); i++) {
            OurCPU cpu = cpuList.getValueByIndex(i);
            if (!cpu.isBusy() && scheduler.hasProcesses()) {
                Process nextProcess = scheduler.getNextProcess();
                cpu.executeProcess(nextProcess);
            }
        }
    }
    
    //Revisar esta monda de funcion 
//    public void handleInterruptions() {
//        for (int i = 0; i < cpuList.getSize(); i++) {
//            OurCPU cpu = cpuList.getValueByIndex(i);
//            if (cpu.hasException()) {
//                Process interruptedProcess = cpu.getCurrentProcess();
//                System.out.println("Proceso " + interruptedProcess.getPcb().getName() + " Genero una interrupcion en OurCPU!");
//                cpu.terminateCurrentProcess();
//                queueManager.addToBlockedQueue(interruptedProcess);
//            }
//        }
//    }
}
