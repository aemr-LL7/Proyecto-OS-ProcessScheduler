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
    private Semaphore tickSemaphore; //Siempre tiene que tener n-1 cantidad de permisos para la cantidad de CPUs activos
    private final SimpleList<ClockListener> listeners;
    private int permissionsRequired = 1;//Permisos requeridos: 1 para 1 procesador, 2 para 2 y 3 para 3
    private final Semaphore mutexSemaphore = new Semaphore(1);

    private Clock() {
        this.currentCycle = 0;
        this.cycleDuration = 1000;
        this.listeners = new SimpleList<>();
        this.setName("Clock Thread");

        //La cantidad de permisos que tenga el semaforo va a depender de cuantos CPUs haya en el sistema. 
        //Si hay 1 CPU, necesitamos iniciar en 1(1 para el CPU y 1 Para el SSOO), si hay 2 CPUs necesitamos 2 y si Hay 3, necesitamos 3 Permisos
        this.tickSemaphore = new Semaphore(0);//Inicializacion para 1 solo CPU
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
                System.out.println("Clock tick: ciclo " + currentCycle +" -----------------------------------------------------------\n");
                currentCycle++;

                this.tickSemaphore.release(this.permissionsRequired);

                //Listeners
                SimpleNode<ClockListener> current = listeners.getpFirst();
                while (current != null) {
                    current.getData().onTick(currentCycle);
                    current = current.getpNext();
                }
                
                
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

    public int getPermissionsRequired() {
        return permissionsRequired;
    }

    public void setPermissionsRequired(int permissionsRequired) {
        this.permissionsRequired = permissionsRequired;
    }

    public Semaphore getMutexSemaphore() {
        return mutexSemaphore;
    }
}
