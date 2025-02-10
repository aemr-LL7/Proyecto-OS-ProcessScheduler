
import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.OperatingSystem;
import Classes.OurCPU;
import Classes.ProcessFactory.Process;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;

import java.util.concurrent.Semaphore;
import Classes.Scheduler.RoundRobin;
import EDD.OurHashTable;
import Main.Clock;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author Windows 11
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here



        // OS se encarga de la planificacion y generacion dinamica de procesos
//        OperatingSystem os_v1 = new OperatingSystem(queueManager, cpusList, scheduler, tickSemaphore);

        // Iniciar el Clock (que es un hilo) para generar ticks periodicos
        Clock clock = Clock.getInstance();
        clock.setCycleDuration(1000);
        clock.start();
    }

}
