/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

import OperativeSystem.QueueManager;
import OperativeSystem.Clock;
import OperativeSystem.ClockListener;
import OperativeSystem.ExceptionHandler;


/**
 *
 * @author Windows 11
 */
public class OurProcess {

    private final PCB pcb;
    private int executedInstructions;

    public OurProcess(PCB pcb) {
        this.pcb = pcb;
        this.executedInstructions = 0;
    }

    public void executeInstruction() throws InterruptedException {
        if (pcb.getState() == ProcessState.FINISHED) {
            System.out.println("[PRCs] Proceso " + pcb.getName() + "Ha terminado su ejecucion");
            return;
        }

        if (pcb.getState() == ProcessState.BLOCKED) {
            System.out.println("[PRCs] Proceso " + pcb.getName() + " esta bloqueado y no puede ejecutar instrucciones");
            return;
        }

        // Marcar el proceso como RUNNING cuando está en ejecución
        pcb.setState(ProcessState.RUNNING);

        executedInstructions++;
        pcb.setPC(pcb.getPC() + 1);

        if (this.hasFinished()) {
            System.out.println("[PRCs] TERMINE DE EJECUTAR MIS INSTRUCCIONES BRAV");
            this.pcb.setState(ProcessState.FINISHED);
        }

        // Verificar si se debe lanzar una interrupción I/O
        if (pcb.isIsIOBound() && executedInstructions % pcb.getExceptionCycleThreshold() == 0) {
            // Llamar al manejador de excepciones para mover el proceso a bloqueados
            handleIOInterruption();
            ExceptionHandler.getInstance().interruptCPU(this);
        }

    }

    private void handleIOInterruption() {
        System.out.println("[PRCs] Proceso " + pcb.getName() + " lanzo una interrupción I/O.");
        pcb.setState(ProcessState.BLOCKED);

        ClockListener temporaryListener = new ClockListener() {
            private final int startCycle = Clock.getInstance().getCurrentCycle();

            @Override
            public void onTick(int newCycle) {
                if (newCycle >= startCycle + pcb.getExceptionSolveNumber()) {

                    if (!hasFinished()) {
                        pcb.setState(ProcessState.READY); // Cambiar el estado a READY
                        QueueManager.getInstance().addToReadyQueue(getPcb()); //HAY DEADLOCK ACA PAPU JijijiJA
                        System.out.println("[PRCs] Proceso " + pcb.getName() + " ha sido desbloqueado y agregado a LISTOS.");
                    }
                    
                    Clock.getInstance().removeListener(this);
                    pcb.setMAR(0);
                }
            }
        };

        Clock.getInstance().addListener(temporaryListener);
    }

    public boolean hasFinished() {
        return this.executedInstructions > this.getPcb().getTotalInstructions();
    }

    /**
     * @return the pcb
     */
    public PCB getPcb() {
        return pcb;
    }

    /**
     * @return the executedInstructions
     */
    public int getExecutedInstructions() {
        return executedInstructions;
    }

    /**
     * @param executedInstructions the executedInstructions to set
     */
    public void setExecutedInstructions(int executedInstructions) {
        this.executedInstructions = executedInstructions;
    }

}
