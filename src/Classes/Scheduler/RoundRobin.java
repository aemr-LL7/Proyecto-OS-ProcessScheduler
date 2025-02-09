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

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    // Agrega el proceso a la cola de listos (con QueueManager)
    @Override
    public void addProcess(Process process) {
        QueueManager.getInstance().addToReadyQueue(process);
        System.out.println("Proceso " + process.getPcb().getName() + " agregado a Round Robin (cola de listos)");
    }

    @Override
    public Process getNextProcess() {
        Process nextProcess = QueueManager.getInstance().getNextReadyProcess();

        if (nextProcess == null) {
            System.out.println("RoundRobin: No hay procesos listos para asignar.");
            return null;
        }

        System.out.println("RoundRobin: Asignando proceso " + nextProcess.getPcb().getName() + " con quantum de " + quantum);

        for (int i = 0; i < quantum && !nextProcess.hasFinished(); i++) {
            nextProcess.executeInstruction();
            if (nextProcess.getPcb().getState() == ProcessState.BLOCKED) {
                System.out.println("RoundRobin: Proceso " + nextProcess.getPcb().getName() + " se bloqueó.");
                return null; // No reencolar si está bloqueado
            }
        }

        if (!nextProcess.hasFinished()) {
            QueueManager.getInstance().addToReadyQueue(nextProcess);
            System.out.println("Proceso " + nextProcess.getPcb().getName() + " se mueve al final de la cola.");
        } else {
            QueueManager.getInstance().addToFinishedProcessesList(nextProcess);
            System.out.println("Proceso " + nextProcess.getPcb().getName() + " ha finalizado.");
        }

        return nextProcess;
    }

    @Override
    public boolean hasProcesses() {
        return !QueueManager.getInstance().getReadyQueue().isEmpty();
    }
}
