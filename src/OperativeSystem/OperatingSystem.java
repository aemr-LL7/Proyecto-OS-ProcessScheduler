/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.ProcessFactory.OurProcess;
import Classes.Scheduler.pFirstComeFirstServed;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;
import EDD.SimpleNode;
import Main.GUI.SimulationUI;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public final class OperatingSystem {

    private static OperatingSystem instance;
    private final DefaultProcessFactory processFactory;
    private SimpleList<OurCPU> cpuList;
    private final Semaphore cpuMutex = new Semaphore(1);
    private Scheduler scheduler;

    private final Clock systemClock = Clock.getInstance();
    private final ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();
    private final QueueManager queueManager = QueueManager.getInstance();

    private final int processSpawnInterval = 5; // Se generan nuevos procesos cada 15 ciclos
    private final int maxReadyQueueSize = 10; // Límite de procesos en cola de listos antes de generar más

    // Constructor privado para evitar instanciación externa
    private OperatingSystem() {
        this.scheduler = new pFirstComeFirstServed(); // Inicializamos con esta porque podemos y ya
        this.processFactory = new DefaultProcessFactory();
//        this.setName("SSOO Thread");
        //this.startSystem();
    }

    // Método para obtener la instancia única
    public static synchronized OperatingSystem getInstance() {
        if (instance == null) {
            instance = new OperatingSystem();
        }
        return instance;
    }

    public void startSystem() {

        getSystemClock().setPermissionsRequired(this.getCpuList().getSize()); //setear la cantidad de permisos a la misma de activos CPUs
        getSystemClock().start();
        this.startAllCPUs();
        System.out.println(" ====> Sistema Operativo iniciado");
    }

    public void initCpuforUI(int numCpus) {

        this.initializeProcessors(SimulationUI.getSimulationUIInstance().getNumCPUs());

    }

    public void initProcessforUI(int numProcesses) {

        this.initializeProcesses(SimulationUI.getSimulationUIInstance().getNumProcesses());
    }

    private void initializeProcessors(int numOfCPUs) {
        SimpleList<OurCPU> auxCpuList = new SimpleList<>();

        for (int i = 0; i < numOfCPUs; i++) {
            OurCPU cpu = new OurCPU(i);
            cpu.setName("CPU" + (i + 1));
            auxCpuList.addAtTheEnd(cpu);
            // init UI
            SimulationUI.getSimulationUIInstance().getCpuListModel().addElement("CPU-" + i);
        }
        // init UI
        SimulationUI.getSimulationUIInstance().getCpusJList().setModel(SimulationUI.getSimulationUIInstance().getCpuListModel());

        this.setCpuList(auxCpuList);
    }

    private void initializeProcesses(int numProcesses) {
        Random random = new Random();

        for (int i = 1; i <= numProcesses; i++) {
            int instructions = random.nextInt(10) + 15; // Entre 15 y 25 instrucciones
            boolean isIOBound = random.nextBoolean(); // Proceso con I/O aleatorio
            int exceptionThreshold = isIOBound ? (random.nextInt(4) + 4) : 0; // Cada cuántas instrucciones lanza una interrupción
            int resolutionCycles = isIOBound ? (random.nextInt(3) + 7) : 0; // Ciclos para resolver I/O

            OurProcess newProcess = processFactory.createProcess("P" + i, instructions, isIOBound, exceptionThreshold, resolutionCycles);

            if (newProcess != null) {
                System.out.println("[OS] Proceso creado -> " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones.");
            } else {
                System.out.println("\nError al crear el proceso P" + i);
            }
        }
    }

    private void startAllCPUs() {
        SimpleNode<OurCPU> current = cpuList.getpFirst();
        while (current != null) {
            OurCPU cpu = current.getData();
            cpu.start();
            current = current.getpNext();
        }
    }

    public void addProcessor() {
        int cpuNumber = this.getCpuList().getSize() + 1;
        String cpuName = "CPU" + cpuNumber;

        // Crear el nuevo CPU y renombrar el thread
        OurCPU newCpu = new OurCPU(cpuNumber);
        newCpu.setName(cpuName);

        // yallready know it - thats curious brav
        this.getCpuList().addAtTheEnd(newCpu);
        System.out.println("[OS] Nuevo CPU añadido: " + newCpu.getCpuId() + "\nList size: " + this.getCpuList().getSize() + "\n");
        this.getCpuList().printList();
    }

    private void removeProcessor() {
        // CÓMO MONDA VAMOS A QUITAR UN PROCESADOR
    }

    public void removeCPU(OurCPU cpu) {
        try {
            this.cpuMutex.acquire();
            this.getSystemClock().getMutexSemaphore().acquire();

            if (this.cpuList.getSize() > 1) {
                this.cpuList.delete(cpu);
                this.getSystemClock().setPermissionsRequired(this.cpuList.getSize());

            } else {

                System.out.println("No puedo matar mas CPUs");

            }

        } catch (InterruptedException e) {
            System.out.println("Oh no");
        } finally {

            this.getSystemClock().getMutexSemaphore().release();
            this.cpuMutex.release();

        }

    }

    public void generateNewProcess() {
        // Verificar si la cola de listos esta "llena"
        if (getQueueManager().getReadyQueueSize() >= maxReadyQueueSize) {
            System.out.println("Cola de listos llena, no se generan nuevos procesos.");
            return;
        }

        // Generar valores aleatorios para el proceso
        Random random = new Random();
        int instructions = random.nextInt(10) + 20; // Entre 20 y 30 instrucciones
        boolean isIOBound = random.nextBoolean();
        int exceptionThreshold = isIOBound ? (random.nextInt(4) + 4) : 0;
        int resolutionCycles = isIOBound ? (random.nextInt(3) + 5) : 0;

        OurProcess newProcess = processFactory.createProcess(
                "P" + this.systemClock.getCurrentCycle(),
                instructions,
                isIOBound,
                exceptionThreshold,
                resolutionCycles
        );

        if (newProcess != null) {
            System.out.println("\n[OS] Nuevo proceso generado -> "
                    + newProcess.getPcb().getName() + " con " + instructions + " instrucciones");
        } else {
            System.out.println("\n[OS] Error al generar el proceso.");
        }
    }

    public OurProcess addNewProcess(String name, int instructions, boolean isIOBound, int exceptionThreshold, int resolutionCycles) {

        OurProcess newProcess = isIOBound ? processFactory.createProcess(name, instructions, isIOBound, exceptionThreshold, resolutionCycles) : processFactory.createProcess(name, instructions, false, 0, 0);

        System.out.println("\n[OS] Nuevo proceso generado -> " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones");
        return newProcess;

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

    /**
     * @return the queueManager
     */
    public QueueManager getQueueManager() {
        return queueManager;
    }

    /**
     * @return the exceptionHandler
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * @return the systemClock
     */
    public Clock getSystemClock() {
        return systemClock;
    }

}
