/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.ProcessState;
import EDD.SimpleNode;
import OperativeSystem.OurCPU;

/**
 *
 * @author Windows 11
 */
public class pShortestJobFirst implements Scheduler {

    @Override
    public OurProcess getNextProcess() {
        try {
            return searchShortestJob();
        } catch (InterruptedException ex) {
            return null;
        }
    }

    public OurProcess searchShortestJob() throws InterruptedException {

        QueueManager queueManager = QueueManager.getInstance();
        try {

            queueManager.getProcessTableSemaphore().acquire();
            queueManager.getReadyQueueSemaphore().acquire();
            queueManager.getNewProcessesQueueSemaphore().acquire();

            SimpleNode<OurProcess> auxNode = queueManager.getProcessTable().getEntriesList().getpFirst();
            if (auxNode != null) {

                OurProcess returning = auxNode.getData();
                while (auxNode != null) {
                    if (auxNode.getData().getPcb().getTotalInstructions() > returning.getPcb().getTotalInstructions() && auxNode.getData().getPcb().getState() == ProcessState.READY || auxNode.getData().getPcb().getState() == ProcessState.NEW) {
                        returning = auxNode.getData();

                        queueManager.getReadyQueue();
                        queueManager.getReadyQueue().remove(returning.getPcb());
                        queueManager.getNewProcessesQueue().remove(returning.getPcb());

                        queueManager.getProcessTableSemaphore().release();
                        queueManager.getReadyQueueSemaphore().release();
                        queueManager.getNewProcessesQueueSemaphore().release();
                        return returning;
                    }

                    auxNode = auxNode.getpNext();
                }
            }

        } catch (InterruptedException e) {
            return null;
        } finally {

            queueManager.getProcessTableSemaphore().release();
            queueManager.getReadyQueueSemaphore().release();
            queueManager.getNewProcessesQueueSemaphore().release();
        }
        return null;

    }

    @Override
    public void checkFlags(OurCPU cpu) {

    }
}
