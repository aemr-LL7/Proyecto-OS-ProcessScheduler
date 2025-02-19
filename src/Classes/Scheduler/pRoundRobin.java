/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.ProcessState;
import EDD.OurQueue;
import OperativeSystem.OurCPU;

/**
 *
 * @author Windows 11
 */
public class pRoundRobin implements Scheduler {

    private final int quantum; // Número de instrucciones (o ciclos) que se asignan a cada proceso
    private QueueManager queueManager = QueueManager.getInstance();

    public pRoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public OurProcess getNextProcess() {
        return null;
    }

    @Override
    public void checkFlags(OurCPU cpu) {

    }
}
