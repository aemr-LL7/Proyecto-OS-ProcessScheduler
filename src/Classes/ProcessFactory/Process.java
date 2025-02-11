/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

import Classes.Scheduler.QueueManager;
import Main.Clock;
import Main.ClockListener;

/**
 *
 * @author Windows 11
 */
public class Process implements ClockListener {

    private final PCB pcb;
    private int executedInstructions;

    public Process(PCB pcb) {
        this.pcb = pcb;
        this.executedInstructions = 0;
        Clock.getInstance().addListener(this); // Se suscribe al Clock
    }

    @Override
    public void onTick(int currentCycle) {
        // Solo ejecutar instrucciones si el proceso esta en RUNNING
        if (pcb.getState() == ProcessState.RUNNING && executedInstructions < pcb.getTotalInstructions()) {
            executeInstruction();
        }
    }

    public void executeInstruction() {
        if (pcb.getState() == ProcessState.BLOCKED) {
            System.out.println("Proceso " + pcb.getName() + " está bloqueado y no puede ejecutar instrucciones.");
            return;
        }
        System.out.println("********"+this.pcb.getName()+ "estoy ejecutando...");

//        // Marcar el proceso como RUNNING cuando está en ejecución
//        pcb.setState(ProcessState.RUNNING);
        executedInstructions++;
        pcb.setPC(pcb.getPC() + 1);

        // Verificar si se debe lanzar una interrupción I/O
        if (pcb.isIsIOBound() && executedInstructions % pcb.getExceptionCycleThreshold() == 0) {
            handleIOInterruption();
        }

    }

    private void handleIOInterruption() {
        System.out.println("Proceso " + pcb.getName() + " lanzo una interrupción I/O.");
        pcb.setState(ProcessState.BLOCKED);

        ClockListener temporaryListener = new ClockListener() {
            private final int startCycle = Clock.getInstance().getCurrentCycle();

            @Override
            public void onTick(int newCycle) {
                if (newCycle >= startCycle + pcb.getExceptionSolveNumber()) {

                    if (!hasFinished()) {
                        pcb.setState(ProcessState.READY); // Cambiar el estado a READY
                        QueueManager.getInstance().addToReadyQueue(getPcb());
                        System.out.println("Proceso " + pcb.getName() + " ha sido desbloqueado y agregado a LISTOS.");
                    }
                    pcb.setMAR(0);
                    Clock.getInstance().removeListener(this);
                }
            }
        };

        Clock.getInstance().addListener(temporaryListener);
    }

    public boolean hasFinished() {
        return executedInstructions >= pcb.getTotalInstructions();
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
