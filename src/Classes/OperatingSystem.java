/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.Scheduler.QueueManager;
import Interfaces.Scheduler;
import EDD.SimpleList;

/**
 *
 * @author Windows 11
 */
public class OperatingSystem implements Runnable{
    private final QueueManager queueManager;
    private final SimpleList<OurCPU> cpuList;
    private final Scheduler scheduler;

    public OperatingSystem(QueueManager queueManager, SimpleList<OurCPU> CPU_List, Scheduler scheduler) {
        this.queueManager = queueManager;
        this.cpuList = CPU_List;
        this.scheduler = scheduler;
    }

    @Override
    public void run(){
        
    }
    
    public void scheduleProcesses() {
    }

    public void handleInterruptions() {
    }
}
