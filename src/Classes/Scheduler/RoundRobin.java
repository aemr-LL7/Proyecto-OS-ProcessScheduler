/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.Process;
import Classes.ProcessFactory.ProcessState;
import EDD.OurQueue;

/**
 *
 * @author Windows 11
 */
public class RoundRobin implements Scheduler {

    private final int quantum; // Número de instrucciones (o ciclos) que se asignan a cada proceso
    private QueueManager queueManager = QueueManager.getInstance();

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public Process getNextProcess() {
        return null;
    }
}
