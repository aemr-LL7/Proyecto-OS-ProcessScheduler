/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes.ProcessFactory;

/**
 *
 * @author Windows 11
 */
public abstract class ProcessFactory {

    protected static int idCounter = 1;

    public abstract Process createProcess(String name, int instructions, boolean isIOBound, int exceptionCycleNumber, int resolutionCycles);

    protected String generateUniqueId() {
        return "u" + idCounter++;
    }
}
