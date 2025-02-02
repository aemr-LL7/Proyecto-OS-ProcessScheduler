
import Classes.CpuManager;
import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.OperatingSystem;
import Classes.ProcessFactory.ProcessFactory;
import Classes.ProcessFactory.Process;
import Classes.Scheduler.FirstComeFirstServed;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.Scheduler;
import Classes.Scheduler.ShortestJobFirst;
import EDD.SimpleList;
import Main.SimulationConfig;

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

        SimulationConfig simuConfig = SimulationConfig.getInstance();
        // Opcional
        simuConfig.setCycleDuration(500);
//        simuConfig.setTotalCycles(20);
//        simuConfig.setNumCPU(2);
//        simuConfig.setCycleQty(1);

        // Maneja ready, blocked, and finished procesos
        QueueManager queueManager = new QueueManager();

        // Instancia de algoritmo de planificacion (ejemplo sjf)
        FirstComeFirstServed scheduler = new FirstComeFirstServed();

        // Implementacion de procesos a crear
        ProcessFactory processFactory = new DefaultProcessFactory();

        // Crear dos procesos iniciales
        Process process1 = processFactory.createProcess("Process1", 10, false, 3, 1);
        Process process2 = processFactory.createProcess("Process2", 15, true, 8, 2);
        Process process3 = processFactory.createProcess("Process3", 15, true, 4, 1);
        Process process4 = processFactory.createProcess("Process4", 15, false, 1, 4);

        // Add processes to the scheduler and the ready queue of the Queue Manager
        scheduler.addProcess(process1);
        scheduler.addProcess(process2);
        scheduler.addProcess(process3);
        scheduler.addProcess(process4);
        queueManager.addToReadyQueue(process1);
        queueManager.addToReadyQueue(process2);
        queueManager.addToReadyQueue(process3);
        queueManager.addToReadyQueue(process4);

        
        CpuManager cpuManager = new CpuManager(simuConfig.getNumCPU());

        // Crear OS para control de planificacion de procesos e interrupciones
        OperatingSystem ourOS = new OperatingSystem(queueManager, cpuManager.getCPUList(), scheduler);

        // Optionally, start the process threads so that they execute their run() method
        // (Depending on the design, the OS scheduling might start or resume these threads)
        process1.start();
        process2.start();

        for (int i = 0; i < simuConfig.getTotalCycles(); i++) {
            System.out.println("Ciclo actual: " + i);

            // Planificar procesos disponibles y las interrupcioens
            ourOS.scheduleProcesses();
            ourOS.handleInterruptions();

            // Esperar a la duracion de un ciclo
            try {
                Thread.sleep(simuConfig.getCycleDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulation finished!");
    }

}
