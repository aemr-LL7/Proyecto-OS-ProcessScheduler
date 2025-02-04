
import Classes.CpuManager;
import Classes.ProcessFactory.DefaultProcessFactory;
import Classes.OperatingSystem;
import Classes.OurCPU;
import Classes.ProcessFactory.ProcessFactory;
import Classes.ProcessFactory.Process;
import Classes.Scheduler.FirstComeFirstServed;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.RoundRobin;
import Classes.Scheduler.Scheduler;
import Classes.Scheduler.ShortestJobFirst;
import EDD.SimpleList;
import Main.SimulationConfig;
import java.util.concurrent.Semaphore;
import Classes.Scheduler.RoundRobin;

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
        
        // Init config
        SimulationConfig configSimulation = SimulationConfig.getInstance();
        
        // Init Components
        QueueManager queueManager = new QueueManager();
        Scheduler scheduler = new RoundRobin(3); // 3 ciclos
        ProcessFactory processFactory = new DefaultProcessFactory();
        Semaphore instructionSemaphore = new Semaphore(1);

        // Create Processes
        Process process1 = processFactory.createProcess("P1", 10, true, 0, 0);
        Process process2 = processFactory.createProcess("P2", 15, false, 3, 2);
        Process process3 = processFactory.createProcess("P3", 8, true, 0, 0);

        scheduler.addProcess(process1);
        scheduler.addProcess(process2);
        scheduler.addProcess(process3);
        queueManager.addToReadyQueue(process1);
        queueManager.addToReadyQueue(process2);
        queueManager.addToReadyQueue(process3);

        // Crear e iniciar cpus | se inician 2 por default
        SimpleList<OurCPU> cpus = new SimpleList<>();
        for (int i = 0; i < configSimulation.getNumCPUs(); i++) {
            OurCPU cpu = new OurCPU(instructionSemaphore);
            cpus.addAtTheEnd(cpu);
            cpu.start();
        }

        // Iniciar la simulacion e init de procesos
        OperatingSystem os = new OperatingSystem(queueManager, cpus, scheduler);
        os.startSimulation();
    }

}
