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
    private final int quantum;

    public RoundRobin(int quantum) {
        this.readyQueue = new OurQueue<>();
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process process) {
        readyQueue.insert(process);
        System.out.println("Proceso " + process.getPcb().getName() + " agregado a la cola de Round Robinnnnnnnn");
    }

    @Override
    public Process getNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        Process nextProcess = readyQueue.pop();

        // Simul por quantum
        System.out.println("Robin) Ejecutando proceso: " + nextProcess.getPcb().getName() + " por " + quantum + " cicloss");

        for (int i = 0; i < quantum && !nextProcess.hasFinished(); i++) {
            nextProcess.executeInstruction();
        }

        // Si el proceso no ha terminado, se vuelve a encolar al final
        if (!nextProcess.hasFinished()) {
            readyQueue.insert(nextProcess);
            System.out.println("Robin) Proceso " + nextProcess.getPcb().getName() + " se mueve al final de la cola");
        } else {
            System.out.println("Robin) Proceso " + nextProcess.getPcb().getName() + " ha terminado");
        }

        return nextProcess;
    }

    @Override
    public boolean hasProcesses() {
        return !readyQueue.isEmpty();
    }
}
