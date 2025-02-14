/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Classes.Scheduler;

import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.OurProcess;

/**
 *
 * @author Windows 11 Revisar metodos para las implementaciones de las politicas
 * de planificacion
 */
public interface Scheduler {

    OurProcess getNextProcess(); // Obtener el siguiente proceso según la política

}
