/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.ProcessFactory.Process;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OperatingSystem {

    private final QueueManager queueManager;
    private final SimpleList<OurCPU> cpusList;
    private final Scheduler scheduler;
    private final Semaphore instructionSemaphore;

    private final DefaultProcessFactory processFactory;
    private int cycleCount;
    private final int processSpawnInterval; // Cada cuantos ciclos se generan nuevos procesos

    public OperatingSystem(QueueManager queueManager, SimpleList<OurCPU> cpus, Scheduler scheduler) {
        this.queueManager = queueManager;
        this.cpusList = cpus;
        this.scheduler = scheduler;
        this.instructionSemaphore = new Semaphore(1); // Controla la ejecucion de instruc

        this.processFactory = new DefaultProcessFactory();
        this.cycleCount = 0;
        this.processSpawnInterval = 5; // Generar nuevos cada 5 ciclos
    }

    private void generateNewProcess() {
        Random random = new Random();
        int instructions = random.nextInt(10) + 5; // Entre 5 y 15 instrucciones
        boolean isIOBound = random.nextBoolean();
        int exceptionThreshold = isIOBound ? (random.nextInt(4) + 2) : 0;
        int resolutionCycles = isIOBound ? (random.nextInt(3) + 1) : 0;

        Process newProcess = processFactory.createProcess("P" + cycleCount, instructions, isIOBound, exceptionThreshold, resolutionCycles);
        scheduler.addProcess(newProcess);
        queueManager.addToReadyQueue(newProcess);

        System.out.println(" * * * * Nuevo proceso generado: " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones.");
    }

    public void startSimulation() {
        System.out.println("'''''' OS v1.0 ''''''\nStarting simulation now...");

        while (true) {
            this.cycleCount++;

            // Generar nuevos procesos cada ciertos ciclos
            if (cycleCount % processSpawnInterval == 0) {
                this.generateNewProcess();
            }
            
            scheduleProcesses();
            handleInterruptions();

            if (allCPUsAreIdle() && queueManager.isEmpty()) {
                System.out.println("Simulacion finalizada: No more active processes");
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("La simulacion encontro un error critico: " + e.getMessage());
                break;
            }
        }

        System.out.println("Simulation stopped...");
    }

    private void scheduleProcesses() {
        for (int i = 0; i < this.getCpusList().getSize(); i++) {
            OurCPU cpu = this.getCpusList().getValueByIndex(i);
            if (!cpu.isBusy() && this.getScheduler().hasProcesses()) {
                Process nextProcess = this.getScheduler().getNextProcess();
                cpu.executeProcess(nextProcess);
            }
        }
    }

    private void handleInterruptions() {
        for (int i = 0; i < this.getCpusList().getSize(); i++) {
            OurCPU cpu = this.getCpusList().getValueByIndex(i);
            if (cpu.isBusy() && cpu.getCurrentProcess().isBlocked()) {
                System.out.println("Manejando proceso BLOQUEADO: " + cpu.getCurrentProcess().getPcb().getName());
                getQueueManager().addToBlockedQueue(cpu.getCurrentProcess());
                cpu.terminateCurrentProcess();
            }
        }
    }

    private boolean allCPUsAreIdle() {
        for (int i = 0; i < getCpusList().getSize(); i++) {
            if (this.getCpusList().getValueByIndex(i).isBusy()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the queueManager
     */
    public QueueManager getQueueManager() {
        return queueManager;
    }

    /**
     * @return the cpusList
     */
    public SimpleList<OurCPU> getCpusList() {
        return cpusList;
    }

    /**
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * @return the instructionSemaphore
     */
    public Semaphore getInstructionSemaphore() {
        return instructionSemaphore;
    }

}
