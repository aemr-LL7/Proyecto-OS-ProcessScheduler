/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.Process;
import EDD.OurQueue;
import EDD.SimpleList;

/**
 *
 * @author Windows 11
 */
public class QueueManager {

    private final OurQueue<Process> readyQueue;
    private final OurQueue<Process> blockedQueue;
    private final SimpleList<Process> finishedProcesses;

    public QueueManager() {
        this.readyQueue = new OurQueue<>();
        this.blockedQueue = new OurQueue<>();
        this.finishedProcesses = new SimpleList<>();
    }

    public void addToReadyQueue(Process process) {
        readyQueue.insert(process);
        System.out.println("Proceso : " + process.getPcb().getName() + " ha sido movido a la cola de LISTOS...");
    }

    public void addToBlockedQueue(Process process) {
        blockedQueue.insert(process);
        System.out.println("Proceso : " + process.getPcb().getName() + " ha sido movido a la cola de BLOQUEADOS...");
    }

    public void addToFinishedProcessesList(Process process) {
        finishedProcesses.addAtTheEnd(process);
         System.out.println("Proceso : " + process.getPcb().getName() + " ha terminado, enviando a lista de terminados...");
    }

    public Process getNextReadyProcess() {
        return readyQueue.isEmpty() ? null : readyQueue.pop();
    }

    public Process getNextBlockedProcess() {
        return blockedQueue.isEmpty() ? null : blockedQueue.pop();
    }

    public boolean hasReadyProcesses() {
        return !readyQueue.isEmpty();
    }

    public boolean hasBlockedProcesses() {
        return !blockedQueue.isEmpty();
    }
}
