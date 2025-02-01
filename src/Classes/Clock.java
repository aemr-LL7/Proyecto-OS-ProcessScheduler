/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import EDD.SimpleList;
import EDD.SimpleNode;
import Interfaces.ClockListener;

/**
 *
 * @author B-St
 */
public class Clock {
    
    private static Clock instance;
    private int currentCycle;
    private int cycleDuration;
    private SimpleList<ClockListener> listeners; 
    
    private Clock(){
        this.currentCycle = 0;
        this.cycleDuration = 1000; //inicial 1s
        this.listeners = new SimpleList<>(); //NEVER IMPLEMENTED
    }        
        
    public static synchronized Clock getInstance(){
        if (instance == null){
            return new Clock();
        }
        return instance;
    }
    
    public void tick(){
        currentCycle++;
        notifyListeners();
    }
    
    private void notifyListeners(){
        
        SimpleNode<ClockListener> current = listeners.getpFirst();
        
        while(current != null){
            current.getData().onTick(currentCycle);
        }
        
    }
    
    public void addListenet(ClockListener newListener){
        listeners.addAtTheEnd(newListener);
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }

    public int getCycleDuration() {
        return cycleDuration;
    }

    public void setCycleDuration(int cycleDuration) {
        this.cycleDuration = cycleDuration;
    }
    
    
    
}
