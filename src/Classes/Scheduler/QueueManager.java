/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.Process;
import EDD.OurHashTable;
import EDD.OurQueue;
import EDD.SimpleList;
import Classes.ProcessFactory.ProcessState;

/**
 *
 * @author Windows 11
 */
public class QueueManager {

    private final OurQueue<Process> readyQueue;
    private final OurQueue<Process> blockedQueue;
    private final SimpleList<PCB> finishedProcesses;
    private final OurHashTable<Process> processTable;

    public QueueManager() {
        this.readyQueue = new OurQueue<>();
        this.blockedQueue = new OurQueue<>();
        this.finishedProcesses = new SimpleList<>();
        this.processTable = new OurHashTable<>();
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty() && blockedQueue.isEmpty();
    }

    public void addToReadyQueue(Process process) {
        readyQueue.insert(process);
        process.getPcb().setState(ProcessState.READY);
        System.out.println("Proceso : " + process.getPcb().getName() + " ha sido movido a la cola de LISTOS...");
    }

    public void addToBlockedQueue(Process process) {
        blockedQueue.insert(process);
        process.getPcb().setState(ProcessState.BLOCKED);
        System.out.println("Proceso : " + process.getPcb().getName() + " ha sido movido a la cola de BLOQUEADOS...");
    }

    // Mueve un proceso de bloqueados a listos cuando se resuelve la interrup
    public void unblockProcess(Process process) {
        if (blockedQueue.remove(process)) {
            addToReadyQueue(process);
            System.out.println("Proceso : " + process.getPcb().getName() + " ha sido DESBLOQUEADO y movido a la cola de LISTOS...");
        }
    }

    // Agrega un PCB a la lista de procesos terminados
    public void addToFinishedProcessesList(PCB processPCB) {
        finishedProcesses.addAtTheEnd(processPCB);
        System.out.println("Proceso : " + processPCB.getName() + " ha terminado, enviando a lista de terminados...");
    }

    public Process getNextReadyProcess() {
        return readyQueue.isEmpty() ? null : readyQueue.pop();
    }

    public Process getNextBlockedProcess() {
        return blockedQueue.isEmpty() ? null : blockedQueue.pop();
    }

    // Verifica si hay procesos listos para ejecución
    public boolean hasReadyProcesses() {
        return !readyQueue.isEmpty();
    }

    // Verifica si hay procesos bloqueados
    public boolean hasBlockedProcesses() {
        return !blockedQueue.isEmpty();
    }

    // Devuelve la tabla de procesos activos
    public OurHashTable<Process> getProcessTable() {
        return processTable;
    }
}
