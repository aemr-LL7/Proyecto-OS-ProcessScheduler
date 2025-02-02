/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Classes.Scheduler;

import Classes.PCB;
import Classes.Process;

/**
 *
 * @author Windows 11
 */
public interface Scheduler {

    void addProcess(PCB process); // Agregar proceso a la cola de listos

    Process getNextProcess(); // Obtener el siguiente proceso según la política

    boolean hasProcesses(); // Verificar si hay procesos pendientes

    // Revisar metodos para las implementaciones de las politicas de planificacion
}
