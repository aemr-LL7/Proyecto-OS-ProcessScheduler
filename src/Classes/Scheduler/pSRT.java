/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.OurProcess;
import EDD.SimpleNode;
import OperativeSystem.OurCPU;

/**
 *
 * @author B-St
 */
public class pSRT implements Scheduler {

    @Override
    public OurProcess getNextProcess() {
        OurProcess selectedProcess = null;
        QueueManager queueManager = QueueManager.getInstance();

        try {
            // Adquirir semáforos
            queueManager.getNewProcessesQueueSemaphore().acquire();
            queueManager.getReadyQueueSemaphore().acquire();
            queueManager.getProcessTableSemaphore().acquire();

            // Obtener el primer nodo de la lista de procesos
            SimpleNode<OurProcess> auxNode = queueManager.getProcessTable().getEntriesList().getpFirst();
            int minRemainingTime = auxNode.getData().getPcb().getPC() - auxNode.getData().getPcb().getTotalInstructions();//Tiempo mas corto inicial

            // Recorrer la lista de procesos
            while (auxNode != null) {
                OurProcess currentProcess = auxNode.getData();
                int remainingTime = currentProcess.getPcb().getTotalInstructions() - currentProcess.getPcb().getPC();

                // Seleccionar el proceso con el menor tiempo restante
                if (remainingTime < minRemainingTime) {
                    minRemainingTime = remainingTime;
                    selectedProcess = currentProcess;
                }

                auxNode = auxNode.getpNext();
            }

        } catch (InterruptedException e) {
            System.out.println("Error en Scheduler pSRT");
        } finally {
            // Liberar semáforos
            queueManager.getNewProcessesQueueSemaphore().release();
            queueManager.getReadyQueueSemaphore().release();
            queueManager.getProcessTableSemaphore().release();
        }

        return selectedProcess;
    }

    @Override
    public void checkFlags(OurCPU cpu) {

    }

}
