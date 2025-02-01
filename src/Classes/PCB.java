/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

/**
 *
 * @author Windows 11
 */
public class PCB {

    private int id;
    private String name;
    private Process proccessRef;

    public PCB(int id, String name, Process processRef) {
        this.id = id;
        this.name = name;
        this.proccessRef = processRef;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Process getProccessRef() {
        return proccessRef;
    }

    public void setProccessRef(Process proccessRef) {
        this.proccessRef = proccessRef;
    }

}
