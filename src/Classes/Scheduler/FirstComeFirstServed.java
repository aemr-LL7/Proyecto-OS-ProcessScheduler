/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.Scheduler;

import Classes.Process;
import EDD.OurQueue;

/**
 *
 * @author Windows 11
 */
public class FirstComeFirstServed implements Scheduler {

    private final OurQueue<Process> processQueue;

    public FirstComeFirstServed() {
        this.processQueue = new OurQueue<>();
    }

    @Override
    public void addProcess(Process process) {
        processQueue.insert(process);
    }

    @Override
    public Process getNextProcess() {
        return processQueue.isEmpty() ? null : processQueue.pop();
    }

    @Override
    public boolean hasProcesses() {
        return !processQueue.isEmpty();
    }

}
