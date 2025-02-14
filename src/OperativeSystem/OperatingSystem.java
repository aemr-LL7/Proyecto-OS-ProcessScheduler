/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.ProcessFactory.OurProcess;
import Classes.Scheduler.FirstComeFirstServed;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;
import EDD.SimpleNode;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public final class OperatingSystem {
    private static OperatingSystem instance;
    private final ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();
    private final QueueManager queueManager = QueueManager.getInstance();
    private SimpleList<OurCPU> cpuList;
    private Scheduler scheduler;
    private final Semaphore tickSemaphore = Clock.getInstance().getTickSemaphore(); // Sincronización con Clock
    private final DefaultProcessFactory processFactory;
    private final Clock systemClock = Clock.getInstance();
    private int cycleCount;

    private final int processSpawnInterval = 5; // Se generan nuevos procesos cada 15 ciclos
    private final int maxReadyQueueSize = 10; // Límite de procesos en cola de listos antes de generar más

    // Constructor privado para evitar instanciación externa
    private OperatingSystem() {
        this.scheduler = new FirstComeFirstServed(); // Inicializamos con esta porque podemos y ya
        this.processFactory = new DefaultProcessFactory();
        this.initializeProcessors();
    }

    // Método para obtener la instancia única
    public static synchronized OperatingSystem getInstance() {
        if (instance == null) {
            instance = new OperatingSystem();
        }
        return instance;
    }

    public void initializeProcessors() {
        SimpleList<OurCPU> cpuList = new SimpleList<>();
        
        // Empezamos la simulación con 2 procesadores
        OurCPU cpu1 = new OurCPU();
        cpu1.setName("CPU1");

        OurCPU cpu2 = new OurCPU();
        cpu2.setName("CPU2");

        cpuList.addAtTheEnd(cpu1);
        cpuList.addAtTheEnd(cpu2);
        
        this.setCpuList(cpuList);
    }

    private void addProcessor() {
        int cpuNumber = this.getCpuList().getSize() + 1;
        String cpuName = "CPU" + cpuNumber;
        
        // Crear el nuevo CPU y renombrar el thread
        OurCPU newCpu = new OurCPU();
        newCpu.setName(cpuName);
        
        // yallready know it
        this.getCpuList().addAtTheEnd(newCpu);
    }
    
    private void removeProcessor() {
        // CÓMO MONDA VAMOS A QUITAR UN PROCESADOR
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

        OurProcess newProcess = processFactory.createProcess("P" + cycleCount, instructions, isIOBound, exceptionThreshold, resolutionCycles);

        System.out.println("Nuevo proceso generado -> " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones");
    }

    private void scheduleProcesses() {
        System.out.println("Intentando asignar procesos en el ciclo " + Clock.getInstance().getCurrentCycle());
        for (int i = 0; i < cpuList.getSize(); i++) {
            OurCPU cpu = cpuList.getValueByIndex(i);
            System.out.println("Estado del CPU-" + i + " -> " + cpu.isIsBusy());
            if (!cpu.isIsBusy()) { // Solo asignar si el CPU está libre
                OurProcess nextProcess = scheduler.getNextProcess();
                if (nextProcess != null) {
                    cpu.executeProcess(nextProcess);
                    System.out.println("==> Asignando proceso " + nextProcess.getPcb().getName() + " al CPU " + i);
                }
            }
        }
    }

    private boolean allCPUsAreIdle() {
        for (int i = 0; i < cpuList.getSize(); i++) {
            if (cpuList.getValueByIndex(i).isIsBusy()) {
                return false;
            }
        }
        return true;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public boolean areCpusIdle() {
        SimpleNode<OurCPU> currentCPU = this.cpuList.getpFirst();

        while (currentCPU != null) {
            if (!currentCPU.getData().isIsBusy()) {
                return true;
            }

            currentCPU = currentCPU.getpNext();
        }

        return false;
    }

    public SimpleList<OurCPU> getCpuList() {
        return cpuList;
    }

    public void setCpuList(SimpleList<OurCPU> cpuList) {
        this.cpuList = cpuList;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }
}
