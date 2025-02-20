/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import OperativeSystem.QueueManager;
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

        OurProcess returning = null;
        try {
            QueueManager queueManager = QueueManager.getInstance();
            queueManager.getProcessTableSemaphore().acquire();
            queueManager.getReadyQueueSemaphore().acquire();
            queueManager.getNewProcessesQueueSemaphore().acquire();

            SimpleNode<OurProcess> auxNode = queueManager.getProcessTable().getEntriesList().getpFirst();
            if (auxNode != null) {

                returning = auxNode.getData();
                while (auxNode != null) {
                    if (auxNode.getData().getPcb().getTotalInstructions() < returning.getPcb().getTotalInstructions() && (auxNode.getData().getPcb().getState() == ProcessState.READY || auxNode.getData().getPcb().getState() == ProcessState.NEW)) {
                        returning = auxNode.getData();
                    }

                    auxNode = auxNode.getpNext();
                }

                if (returning.getPcb().getState() == ProcessState.READY) {
                    queueManager.getReadyQueue().remove(returning.getPcb());
                } else if (returning.getPcb().getState() == ProcessState.NEW) {
                    queueManager.getNewProcessesQueue().remove(returning.getPcb());
                }

            }

        } catch (InterruptedException e) {
            return null;
        } finally {

            QueueManager.getInstance().getProcessTableSemaphore().release();
            QueueManager.getInstance().getReadyQueueSemaphore().release();
            QueueManager.getInstance().getNewProcessesQueueSemaphore().release();

        }
        return returning;
    }

    @Override
    public void checkFlags(OurCPU cpu) {

    }
}
