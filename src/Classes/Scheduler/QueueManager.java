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
import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 */
public class QueueManager {

    private final OurQueue<PCB> readyQueue;
    private final OurQueue<PCB> blockedQueue;
    private final OurQueue<PCB> suspendedQueue;
    private final OurQueue<PCB> newProcessesQueue;
    private final SimpleList<PCB> finishedProcesses;
    private final OurHashTable<Process> processTable;

    // Semáforos para proteger cada cola
    private final Semaphore readyQueueSemaphore;
    private final Semaphore blockedQueueSemaphore;
    private final Semaphore finishedQueueSemaphore;
    private final Semaphore suspendedQueueSemaphore;
    private final Semaphore processTableSemaphore;
    private final Semaphore newProcessesQueueSemaphore;

    private static QueueManager queueInstance = null;

    private QueueManager() {
        this.readyQueue = new OurQueue<>();
        this.blockedQueue = new OurQueue<>();
        this.suspendedQueue = new OurQueue<>();
        this.newProcessesQueue = new OurQueue<>();
        this.finishedProcesses = new SimpleList<>();
        this.processTable = new OurHashTable<>();

        this.readyQueueSemaphore = new Semaphore(1);
        this.blockedQueueSemaphore = new Semaphore(1);
        this.finishedQueueSemaphore = new Semaphore(1);
        this.suspendedQueueSemaphore = new Semaphore(1);
        this.processTableSemaphore = new Semaphore(1);
        this.newProcessesQueueSemaphore = new Semaphore(1);
    }

    public static synchronized QueueManager getInstance() {
        if (getQueueInstance() == null) {
            queueInstance = new QueueManager();
        }
        return getQueueInstance();
    }

    public void addToReadyQueue(PCB process) {
        try {
            readyQueueSemaphore.acquire();

            if (process.getState() == ProcessState.BLOCKED) {
                System.out.println("ERROR: Intentando agregar un proceso bloqueado a la cola de listos: " + process.getName());
                return;
            }

            process.setState(ProcessState.READY);
            readyQueue.insert(process);

            System.out.println("****** Proceso " + process.getName() + " agregado a la cola de LISTOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readyQueueSemaphore.release();
        }
    }

    public void addToBlockedQueue(PCB process) {
        try {
            getBlockedQueueSemaphore().acquire();

            process.setState(ProcessState.BLOCKED);
            getBlockedQueue().insert(process);

            System.out.println("Proceso " + process.getName() + " agregado a la cola de BLOQUEADOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getBlockedQueueSemaphore().release();
        }
    }

    public void addToFinishedProcessesList(PCB process) {
        try {
            getFinishedQueueSemaphore().acquire();

            getFinishedProcesses().addAtTheEnd(process);
            System.out.println("∎ Proceso " + process.getName() + " finalizado, movido a la lista de TERMINADOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getFinishedQueueSemaphore().release();
        }
    }

    public PCB getNextReadyProcess() {
        try {
            readyQueueSemaphore.acquire();
            if (readyQueue.isEmpty()) {
                return null;
            }

            PCB process = readyQueue.pop();
            process.setState(ProcessState.RUNNING); // CAMBIAR ESTADO A RUNNING
            readyQueueSemaphore.release();
            return process;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isEmpty() {
        try {
            getReadyQueueSemaphore().acquire();
            getBlockedQueueSemaphore().acquire();
            return getReadyQueue().isEmpty() && getBlockedQueue().isEmpty();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return true;
        } finally {
            getReadyQueueSemaphore().release();
            getBlockedQueueSemaphore().release();
        }
    }

    public Process getProcessByPCB(PCB processRef) throws InterruptedException {
        if (processRef == null) {
            System.out.println("ERROR: Se intento buscar un proceso nullo");
        }
        this.readyQueueSemaphore.acquire();
        this.processTableSemaphore.acquire();

        //PCB pcoressPcb = this.readyQueue.pop();
        Process process = processTable.get(processRef.getId());

        this.readyQueueSemaphore.release();
        this.processTableSemaphore.release();
        if (process == null) {
            System.out.println("ERROR: No se encontro el proceso en la tabla de procesos");
        }
        
        return process;
    }

    public void hashProcess(Process process) throws InterruptedException {
        this.processTableSemaphore.acquire();

        this.processTable.put(process.getPcb().getId(), process);

        this.processTableSemaphore.release();
    }

    public void addNewProcess(Process process) throws InterruptedException {

        this.hashProcess(process);
        this.addToReadyQueue(process.getPcb());

    }

    public OurHashTable<Process> getProcessTable() {
        return processTable;
    }

    public int getReadyQueueSize() {
        return getReadyQueue().getSize();
    }

    /**
     * @return the readyQueue
     */
    public OurQueue<PCB> getReadyQueue() {
        return readyQueue;
    }

    /**
     * @return the blockedQueue
     */
    public OurQueue<PCB> getBlockedQueue() {
        return blockedQueue;
    }

    /**
     * @return the finishedProcesses
     */
    public SimpleList<PCB> getFinishedProcesses() {
        return finishedProcesses;
    }

    /**
     * @return the readyQueueSemaphore
     */
    public Semaphore getReadyQueueSemaphore() {
        return readyQueueSemaphore;
    }

    /**
     * @return the blockedQueueSemaphore
     */
    public Semaphore getBlockedQueueSemaphore() {
        return blockedQueueSemaphore;
    }

    /**
     * @return the finishedQueueSemaphore
     */
    public Semaphore getFinishedQueueSemaphore() {
        return finishedQueueSemaphore;
    }

    /**
     * @return the queueInstance
     */
    public static QueueManager getQueueInstance() {
        return queueInstance;
    }

    public Semaphore getProcessTableSemaphore() {
        return this.processTableSemaphore;
    }

    public OurQueue<PCB> getSuspendedQueue() {
        return suspendedQueue;
    }

    public OurQueue<PCB> getNewProcessesQueue() {
        return newProcessesQueue;
    }

    public Semaphore getSuspendedQueueSemaphore() {
        return suspendedQueueSemaphore;
    }

    public Semaphore getNewProcessesQueueSemaphore() {
        return newProcessesQueueSemaphore;
    }

}
