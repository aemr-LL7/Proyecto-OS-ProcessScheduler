
import OperativeSystem.OurCPU;
import OperativeSystem.Clock;
import OperativeSystem.OperatingSystem;


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
        OperatingSystem ourOS = OperatingSystem.getInstance();
        ourOS.startSystem();
        //ourOS.scheduleProcesses();
        //ourOS.addProcessor();

    }

}
