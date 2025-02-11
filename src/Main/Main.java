
import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.OperatingSystem;
import Classes.OurCPU;
import Classes.ProcessFactory.Process;
import Classes.Scheduler.FirstComeFirstServed;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import EDD.SimpleList;

import java.util.concurrent.Semaphore;
import Classes.Scheduler.RoundRobin;
import EDD.OurHashTable;
import Main.Clock;
import java.util.Random;

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
        QueueManager queueManager = QueueManager.getInstance();

        int numProcesses = 2;
        DefaultProcessFactory processFactory = new DefaultProcessFactory();
        Random random = new Random();

        for (int i = 1; i <= numProcesses; i++) {
            int instructions = random.nextInt(10) + 5; // Entre 5 y 15 instrucciones
            boolean isIOBound = random.nextBoolean(); // Proceso con I/O aleatorio
            int exceptionThreshold = isIOBound ? (random.nextInt(4) + 2) : 0; // Cada cuántas instrucciones lanza una interrupción
            int resolutionCycles = isIOBound ? (random.nextInt(3) + 1) : 0; // Ciclos para resolver I/O

            Process newProcess = processFactory.createProcess("P" + i, instructions, isIOBound, exceptionThreshold, resolutionCycles);

            if (newProcess != null) {
                System.out.println("Proceso creado -> " + newProcess.getPcb().getName() + " con " + instructions + " instrucciones.");
            } else {
                System.out.println("Error al crear el proceso P" + i);
            }
        }

        // Crear lista de CPUs
        int numCPUs = 2;
        SimpleList<OurCPU> cpuList = new SimpleList<>();
        Semaphore instructionSemaphore = new Semaphore(1);
        Semaphore tickSemaphore = new Semaphore(0);

        for (int i = 0; i < numCPUs; i++) {
            OurCPU cpu = new OurCPU(instructionSemaphore, tickSemaphore);
            cpuList.addAtTheEnd(cpu);
            cpu.start();
        }

        // Seleccionar el algoritmo de planificacion
        Scheduler scheduler = new FirstComeFirstServed();

        // Inicializar el sistema operativo
        OperatingSystem ourOS = new OperatingSystem(queueManager, cpuList, scheduler, tickSemaphore);

        // Iniciar el Clock
        Clock clock = Clock.getInstance();
        clock.setCycleDuration(1000); // 1 segundo por ciclo
        clock.start();

    }

}
