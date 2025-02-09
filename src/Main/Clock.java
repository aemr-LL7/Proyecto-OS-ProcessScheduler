/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import EDD.SimpleList;
import EDD.SimpleNode;

/**
 *
 * @author Windows 11
 */
public class Clock extends Thread {

    private static Clock instance;
    private int currentCycle;
    private int cycleDuration; // Duracion de cada ciclo, en ms
    private final SimpleList<ClockListener> listeners;

    private Clock() {
        this.currentCycle = 0;
        this.cycleDuration = 1000;
        this.listeners = new SimpleList<>();
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
            currentCycle++;
            // Notifica a cada listener
            System.out.println("Clock tick: ciclo " + currentCycle);
            SimpleNode<ClockListener> current = listeners.getpFirst();
            while (current != null) {
                current.getData().onTick(currentCycle);
                current = current.getpNext();
            }
            try {
                Thread.sleep(cycleDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

}
