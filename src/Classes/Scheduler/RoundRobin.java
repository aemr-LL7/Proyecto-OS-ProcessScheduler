/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.Process;
import EDD.OurQueue;

/**
 *
 * @author Windows 11
 */
public class RoundRobin implements Scheduler {

    private final OurQueue<Process> readyQueue;
    private final OurQueue<Process> blockedQueue;

    private final int quantum; // num de instrucciones (o ciclos) que se asignan a cada proceso

    public RoundRobin(int quantum) {
        this.readyQueue = QueueManager.getInstance().getReadyQueue();
        this.blockedQueue = QueueManager.getInstance().getBlockedQueue();
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process process) {
        readyQueue.insert(process);
        System.out.println("Proceso " + process.getPcb().getName() + " agregado a la cola de Round Robin.");
    }

    @Override
    public Process getNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        Process nextProcess = readyQueue.pop();
        System.out.println("RoundRobin: Asignando proceso " + nextProcess.getPcb().getName() + " por un quantum de " + quantum + " instrucciones.");

        // Ejecutar quantum de instrucciones
        for (int i = 0; i < quantum && !nextProcess.hasFinished(); i++) {
            nextProcess.executeInstruction();
        }

        // Si el proceso ha finalizado, NO se vuelve a encolar
        if (nextProcess.hasFinished()) {
            System.out.println("Proceso " + nextProcess.getPcb().getName() + " ha finalizado y será eliminado del sistema.");
            QueueManager.getInstance().addToFinishedProcessesList(nextProcess.getPcb());
        } else {
            readyQueue.insert(nextProcess);
            System.out.println("Proceso " + nextProcess.getPcb().getName() + " se mueve al final de la cola.");
        }

        return nextProcess;
    }

    @Override
    public boolean hasProcesses() {
        return !readyQueue.isEmpty();
    }
}
