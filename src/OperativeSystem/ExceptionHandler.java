/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperativeSystem;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Windows 11
 * => Clase para manejar las interrupciones generadas por procesos consumidores de operaciones i/o 
 */
public class ExceptionHandler {
    // Instancia única de la clase
    private static ExceptionHandler instance;
    private final Semaphore mutex = new Semaphore(1);

    // Constructor privado para evitar instanciación externa
    private ExceptionHandler() {}

    // Método estático para obtener la instancia única
    public static synchronized ExceptionHandler getInstance() {
        if (instance == null) {
            instance = new ExceptionHandler();
        }
        return instance;
    }
    
    public void IOInterruption(){
        
    }
    
}

