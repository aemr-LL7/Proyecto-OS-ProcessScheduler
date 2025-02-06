
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

        // Imita la memoria principal?
        OurHashTable<Process> mainMemo = new OurHashTable<>();

        QueueManager queueManager = QueueManager.getInstance();

        Scheduler scheduler = new RoundRobin(5);

        DefaultProcessFactory processFactory = new DefaultProcessFactory();

        // Proteccion de seccion criticas en el CPU
        Semaphore instructionSemaphore = new Semaphore(1);

        // CPUS SYNC con el Clock
        Semaphore tickSemaphore = new Semaphore(0);  // Inicialmente en 0 -> sera liberado por el Clock

        Process process1 = processFactory.createProcess("P1", 10, true, 10, 8);
        Process process2 = processFactory.createProcess("P2", 15, false, 3, 2);
        Process process3 = processFactory.createProcess("P3", 8, true, 9, 1);
        Process process4 = processFactory.createProcess("P4", 8, false, 16, 3);
        Process process5 = processFactory.createProcess("P5", 8, false, 2, 5);

        mainMemo.put(process1.getPcb().getId(), process1);
        mainMemo.put(process2.getPcb().getId(), process2);
        mainMemo.put(process3.getPcb().getId(), process3);
        mainMemo.put(process4.getPcb().getId(), process4);
        mainMemo.put(process5.getPcb().getId(), process5);

        scheduler.addProcess(process1);
        scheduler.addProcess(process2);
        scheduler.addProcess(process3);
        scheduler.addProcess(process4);
        scheduler.addProcess(process5);
        queueManager.addToReadyQueue(process1);
        queueManager.addToReadyQueue(process2);
        queueManager.addToReadyQueue(process3);
        queueManager.addToReadyQueue(process4);
        queueManager.addToReadyQueue(process5);

        // Crear una lista de CPU ejemplo 2
        SimpleList<OurCPU> cpus = new SimpleList<>();
        for (int i = 0; i < 2; i++) {
            OurCPU cpu = new OurCPU(instructionSemaphore, tickSemaphore);
            cpus.addAtTheEnd(cpu);
            // iniciar funcionamiento de cpus
            cpu.start();
        }

        // OS se encarga de la planificacion y generacion dinamica de procesos
        OperatingSystem os_v1 = new OperatingSystem(queueManager, cpus, scheduler, tickSemaphore);

        // Iniciar el Clock (que es un hilo) para generar ticks periodicos
        Clock clock = Clock.getInstance();
        clock.setCycleDuration(1000);
        clock.start();
    }

}
