/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import EDD.SimpleList;

/**
 *
 * @author Windows 11
 * => Gestion de CPUs y SMP para la asignacion de procesos entre varios cpus
 */
public class CpuManager {

    private final SimpleList<OurCPU> cpuList;

    public CpuManager(int numCPUS) {
        this.cpuList = new SimpleList<>();
        for (int i = 0; i < numCPUS; i++) {
            cpuList.addAtTheEnd(new OurCPU());
        }
    }
    
    public SimpleList<OurCPU> getCPUList(){
        return this.cpuList.isEmpty() ? null: this.cpuList;
    }
}
