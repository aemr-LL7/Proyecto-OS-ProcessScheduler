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
import Main.Clock;
import Main.ClockListener;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class OperatingSystem implements ClockListener {

    private final QueueManager queueManager;
    private final SimpleList<OurCPU> cpuList;
    private Scheduler scheduler;
    private final Semaphore instructionSemaphore;
    private final Semaphore tickSemaphore; // Sincronización con Clock
    private final DefaultProcessFactory processFactory;
    private int cycleCount;

    private final int processSpawnInterval = 5; // Se generan nuevos procesos cada 15 ciclos
    private final int maxReadyQueueSize = 10; // Limite de procesos en cola de listos antes de generar mas

    public OperatingSystem(QueueManager queueManager, SimpleList<OurCPU> cpus, Scheduler scheduler, Semaphore tickSemaphore) {
        this.queueManager = queueManager;
        this.cpuList = cpus;
        this.scheduler = scheduler;
        this.instructionSemaphore = new Semaphore(1);
        this.tickSemaphore = tickSemaphore;
        this.processFactory = new DefaultProcessFactory();
        this.cycleCount = 0;
        Clock.getInstance().addListener(this);
    }

    @Override
    public void onTick(int currentCycle) {
        cycleCount++;

        // Generar nuevos procesos cada cierto num de ciclos, pero solo si hay espacio en la cola de listos
        if (cycleCount % processSpawnInterval == 0 && queueManager.getReadyQueueSize() < maxReadyQueueSize) {
            generateNewProcess();
        }

        scheduleProcesses();
        // handleInterruptions();

        if (allCPUsAreIdle() && queueManager.isEmpty()) {
            System.out.println("¡? Simulacion finalizada: No hay mas procesos activos.");
            System.exit(0);
        }
    }

    private void generateNewProcess() {
        if (queueManager.getReadyQueueSize() >= maxReadyQueueSize) {
            System.out.println("Cola de listos llena, no se generan nuevos procesos.");
            return;
        }

        Random random = new Random();
        int instructions = random.nextInt(10) + 5; // Entre 5 y 15 instrucciones
        boolean isIOBound = random.nextBoolean();
        int exceptionThreshold = isIOBound ? (random.nextInt(4) + 2) : 0;
        int resolutionCycles = isIOBound ? (random.nextInt(3) + 1) : 0;

        Process newProcess = processFactory.createProcess("P" + cycleCount, instructions, isIOBound, exceptionThreshold, resolutionCycles);
        scheduler.addProcess(newProcess);
        queueManager.addToReadyQueue(newProcess);

        System.out.println("Nuevo proceso generado -> " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones");
    }

    private void scheduleProcesses() {
        System.out.println("Intentando asignar procesos en el ciclo " + Clock.getInstance().getCurrentCycle());
        for (int i = 0; i < cpuList.getSize(); i++) {
            OurCPU cpu = cpuList.getValueByIndex(i);
            System.out.println("CPU " + i + " está ocupado: " + cpu.isBusy());
            if (!cpu.isBusy() && scheduler.hasProcesses()) {
                Process nextProcess = scheduler.getNextProcess();
                if (nextProcess != null) {
                    System.out.println("Asignando proceso " + nextProcess.getPcb().getName() + " al CPU " + i);
                    cpu.executeProcess(nextProcess);
                } else {
                    System.out.println("No hay procesos disponibles para asignar al CPU " + i);
                }
            } else if (cpu.isBusy()) {
                System.out.println("CPU " + i + " sigue ocupado");
            }
        }
    }

    private boolean allCPUsAreIdle() {
        for (int i = 0; i < cpuList.getSize(); i++) {
            if (cpuList.getValueByIndex(i).isBusy()) {
                return false;
            }
        }
        return true;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
