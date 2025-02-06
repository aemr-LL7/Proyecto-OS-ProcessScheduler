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

    private final OurQueue<Process> readyQueue;
    private final OurQueue<Process> blockedQueue;
    private final SimpleList<PCB> finishedProcesses;
    private final OurHashTable<Process> processTable;

    // Semáforos para proteger cada cola
    private final Semaphore readyQueueSemaphore;
    private final Semaphore blockedQueueSemaphore;
    private final Semaphore finishedQueueSemaphore;
    
    private static QueueManager queueInstance = null;

    private QueueManager() {
        this.readyQueue = new OurQueue<>();
        this.blockedQueue = new OurQueue<>();
        this.finishedProcesses = new SimpleList<>();
        this.processTable = new OurHashTable<>();
        this.readyQueueSemaphore = new Semaphore(1);
        this.blockedQueueSemaphore = new Semaphore(1);
        this.finishedQueueSemaphore = new Semaphore(1);
    }
    
    public static synchronized QueueManager getInstance() {
        if (getQueueInstance() == null) {
            queueInstance = new QueueManager();
        }
        return getQueueInstance();
    }

    public void addToReadyQueue(Process process) {
        try {
            getReadyQueueSemaphore().acquire();
            getReadyQueue().insert(process);
            process.getPcb().setState(ProcessState.READY);
            System.out.println("Proceso " + process.getPcb().getName() + " agregado a la cola de LISTOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getReadyQueueSemaphore().release();
        }
    }

    public void addToBlockedQueue(Process process) {
        try {
            getBlockedQueueSemaphore().acquire();
            getBlockedQueue().insert(process);
            process.getPcb().setState(ProcessState.BLOCKED);
            System.out.println("Proceso " + process.getPcb().getName() + " agregado a la cola de BLOQUEADOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getBlockedQueueSemaphore().release();
        }
    }

    public void addToFinishedProcessesList(PCB processPCB) {
        try {
            getFinishedQueueSemaphore().acquire();
            getFinishedProcesses().addAtTheEnd(processPCB);
            System.out.println("Proceso " + processPCB.getName() + " finalizado, movido a la lista de TERMINADOS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getFinishedQueueSemaphore().release();
        }
    }

    public Process getNextReadyProcess() {
        try {
            getReadyQueueSemaphore().acquire();
            return getReadyQueue().isEmpty() ? null : getReadyQueue().pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            getReadyQueueSemaphore().release();
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

    public OurHashTable<Process> getProcessTable() {
        return processTable;
    }

    public int getReadyQueueSize() {
        return getReadyQueue().getSize();
    }

    /**
     * @return the readyQueue
     */
    public OurQueue<Process> getReadyQueue() {
        return readyQueue;
    }

    /**
     * @return the blockedQueue
     */
    public OurQueue<Process> getBlockedQueue() {
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
    
    
}
