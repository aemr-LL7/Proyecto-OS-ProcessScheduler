/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.PCB;
import Classes.Process;
import EDD.OurHashTable;
import EDD.OurQueue;
import EDD.SimpleList;

/**
 *
 * @author Windows 11
 */
public class QueueManager {

    private final OurQueue<PCB> readyQueue;
    private final OurQueue<PCB> blockedQueue;
    private final SimpleList<PCB> finishedProcesses;
    private final OurHashTable<Process> processTable;

    public QueueManager() {
        this.readyQueue = new OurQueue<>();
        this.blockedQueue = new OurQueue<>();
        this.finishedProcesses = new SimpleList<>();
        this.processTable = new OurHashTable<>();
    }

    public void addToReadyQueue(PCB process) {
        readyQueue.insert(process);
        System.out.println("Proceso : " + process.getName() + " ha sido movido a la cola de LISTOS...");
    }

    public void addToBlockedQueue(PCB process) {
        blockedQueue.insert(process);
        System.out.println("Proceso : " + process.getName() + " ha sido movido a la cola de BLOQUEADOS...");
    }

    public void addToFinishedProcessesList(PCB process) {
        finishedProcesses.addAtTheEnd(process);
         System.out.println("Proceso : " + process.getName() + " ha terminado, enviando a lista de terminados...");
    }

    public PCB getNextReadyProcess() {
        return readyQueue.isEmpty() ? null : readyQueue.pop();
    }

    public PCB getNextBlockedProcess() {
        return blockedQueue.isEmpty() ? null : blockedQueue.pop();
    }

    public boolean hasReadyProcesses() {
        return !readyQueue.isEmpty();
    }

    public boolean hasBlockedProcesses() {
        return !blockedQueue.isEmpty();
    }

    public OurHashTable<Process> getProcessTable() {
        return processTable;
    }
    
}
