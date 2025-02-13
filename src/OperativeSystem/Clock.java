/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import EDD.SimpleList;
import EDD.SimpleNode;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows 11
 */
public class Clock extends Thread {

    private static Clock instance;
    private int currentCycle;
    private int cycleDuration; // Duracion de cada ciclo, en ms
    private final Semaphore tickSemaphore = new Semaphore(0);
    private final SimpleList<ClockListener> listeners;

    private Clock() {
        this.currentCycle = 0;
        this.cycleDuration = 500;
        this.listeners = new SimpleList<>();
        this.setName("Clock Thread");
    }

    public static synchronized Clock getInstance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    public void addListener(ClockListener listener) {
        listeners.addAtTheEnd(listener);
    }

    public void removeListener(ClockListener listenerToRemove) {
        listeners.delete(listenerToRemove);
    }

    @Override
    public void run() {
        while (true) {
            try {

                // Notifica a cada listener
                System.out.println("Clock tick: ciclo " + currentCycle);
                currentCycle++;
                this.tickSemaphore.release();

                //Listeners
//            SimpleNode<ClockListener> current = listeners.getpFirst();
//            while (current != null) {
//                current.getData().onTick(currentCycle);
//                current = current.getpNext();
//            }
//            try {
//                Thread.sleep(cycleDuration);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                Thread.sleep(cycleDuration);
            } catch (InterruptedException ex) {
                Logger.getLogger(Clock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @return the currentCycle
     */
    public int getCurrentCycle() {
        return currentCycle;
    }

    /**
     * @param currentCycle the currentCycle to set
     */
    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }

    /**
     * @return the cycleDuration
     */
    public int getCycleDuration() {
        return cycleDuration;
    }

    /**
     * @param cycleDuration the cycleDuration to set
     */
    public void setCycleDuration(int cycleDuration) {
        this.cycleDuration = cycleDuration;
    }

    /**
     * @return the listeners
     */
    public SimpleList<ClockListener> getListeners() {
        return listeners;
    }

    public Semaphore getTickSemaphore() {
        return tickSemaphore;
    }

}
