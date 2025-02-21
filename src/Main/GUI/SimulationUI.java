/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main.GUI;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.ProcessState;
import OperativeSystem.QueueManager;
import Classes.Scheduler.pFirstComeFirstServed;
import Classes.Scheduler.pHRRN;
import Classes.Scheduler.pRoundRobin;
import Classes.Scheduler.pSRT;
import Classes.Scheduler.pShortestJobFirst;
import EDD.OurQueue;
import EDD.SimpleList;
import EDD.SimpleNode;
import FileManager.FileManager;
import OperativeSystem.OperatingSystem;
import OperativeSystem.OurCPU;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Windows 11
 */
public class SimulationUI extends javax.swing.JFrame {

    private static SimulationUI simulationUiInstance;
    private DefaultListModel<String> cpuListModel;
    private DefaultTableModel processTableModel;

    private final QueueManager queueManager = QueueManager.getInstance();
    private final OperatingSystem operatingSystem = OperatingSystem.getInstance();

    private int cycleDuration = 0;
    private int numCPUs = 0;
    private int numProcesses = 0;
    private boolean confirmed = false;
    private boolean startSimulation = false;

    // Archivos params
    private static FileManager fileManager = new FileManager();

    /**
     * Creates new form Simulation
     */
    public SimulationUI() {
        initComponents();
        // gui properties
        this.setTitle("Planificador de Procesos");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1320, 768);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        // Configurar Slider de velocidad 
        this.cycleDurationSlider.setOpaque(false);
        this.cycleDurationSlider.setMinimum(500);
        this.cycleDurationSlider.setMaximum(5000);

        // modelos para las colas y panel de cpus
        this.initQueuePanels();
        // modelo para la tabla de cpus y procesos
        cpuListModel = new DefaultListModel<>();
        cpusJList.setFixedCellHeight(20);
        scrollCpusPane.setPreferredSize(new Dimension(200, 100));
        processTableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Estado", "Instrucciones Totales", "I/O Bound", "PC", "MAR", "Ciclos para I/O", "Ciclos para completar I/O"}, 0);
        processDetailsPanel.setLayout(new BoxLayout(processDetailsPanel, BoxLayout.Y_AXIS));

    }

    public static synchronized SimulationUI getSimulationUIInstance() {
        if (getSimulationUiInstance() == null) {
            setSimulationUiInstance(new SimulationUI());
        }
        return getSimulationUiInstance();
    }

    public void clearAllFields() {
        // parametros de config
        this.setCycleDuration(0);
        this.setNumCPUs(0);
        this.setNumProcesses(0);

        // elementos de simulacion:
        // cpus y colas
        if (!operatingSystem.getCpuList().isEmpty()) {
            getOperatingSystem().getCpuList().wipeList();
        }

        if (!queueManager.isEmpty()) {
            getQueueManager().getReadyQueue().clearQueue();
        }

    }

    private void gottaStartSimulation(int typeScheduler) {
        // Seleccionar el scheduler basado en el tipo
        this.selectScheduler(typeScheduler);

        // Deshabilitar botones iniciales
        this.createButton.setEnabled(false);
        this.startButton.setEnabled(false);

        // Cerrar diálogo de inicio de simulación
        this.startSimulationDialog.dispose();

        // Configurar otros elementos de la interfaz
        this.createCpusButton.setEnabled(false);
        this.deleteCpusButton.setEnabled(false);
        this.saveDataMenuItem.setEnabled(false);
        this.writeDataMenuItem.setEnabled(false);
        this.simulationOptions.setEnabled(true);

        // INICIAR SIMULACION
        this.startSimulation = true;
        this.changeSchedulerMenuItem.setEnabled(true);
        this.getOperatingSystem().startSystem();
    }

    private void selectScheduler(int typeScheduler) {
        switch (typeScheduler) {
            case 1:
                this.getOperatingSystem().setScheduler(new pFirstComeFirstServed());
                break;
            case 3:
                this.getOperatingSystem().setScheduler(new pShortestJobFirst());
                break;
            case 4:
                this.getOperatingSystem().setScheduler(new pSRT());
                break;
            case 5:
                this.getOperatingSystem().setScheduler(new pHRRN());
                break;
            default:
                throw new IllegalArgumentException("Tipo de scheduler no válido: " + typeScheduler);
        }
    }

    private void showConfigDialog(Frame parent) {
        this.configDialog.setTitle("Configuración");
        this.configDialog.setSize(423, 362);
        this.configDialog.setLocationRelativeTo(parent);
        this.configDialog.setVisible(true);
    }

    private void showStartSimulationDialog(Frame parent) {
        this.startSimulationDialog.setTitle("Iniciar Simulación");
        this.startSimulationDialog.setSize(633, 310);
        this.startSimulationDialog.setLocationRelativeTo(parent);
        this.startSimulationDialog.setVisible(true);
    }

    private void showCreateProcessDialog(Frame parent) {
        this.createProcessDialog.setTitle("Crear Nuevo Proceso");
        this.createProcessDialog.setSize(595, 355);
        this.createProcessDialog.setLocationRelativeTo(parent);
        this.createProcessDialog.setVisible(true);
    }

    private void addCPUToSimulation() {
        int cpuCount = getOperatingSystem().getCpuList().getSize();

        // Verificar si ya tenemos el máximo permitido (por ejemplo, 3 CPUs)
        if (cpuCount >= 3) {
            JOptionPane.showMessageDialog(this, "No se pueden añadir más de 3 CPUs.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear nuevo CPU
        getOperatingSystem().addProcessor();
        this.setNumCPUs(cpuCount);

        // Obtener el último CPU añadido
        OurCPU newCpu = getOperatingSystem().getCpuList().getValueByIndex(cpuCount - 1);

        if (newCpu != null) {
            //newCpu.start(); // Iniciar el hilo del nuevo CPU
            this.updateCPUDisplays(); // Refrescar la UI
            this.updateCPUList();
            JOptionPane.showMessageDialog(this, "Nuevo CPU añadido con éxito!", "Añadir CPUs", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al añadir un nuevo CPU.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeCPUFromSimulation() {
//        int selectedIndex = cpusJList.getSelectedIndex();
//
//        if (selectedIndex == -1) {
//            JOptionPane.showMessageDialog(this, "Seleccione un CPU para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

        int cpuCount = getOperatingSystem().getCpuList().getSize();

        if (cpuCount <= 1) {
            System.out.println("=>" + "NO ME ELIMINEI LOQUETE");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el CPU " + cpuCount + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener el último CPU de la lista
            OurCPU lastCpu = getOperatingSystem().getCpuList().getValueByIndex(cpuCount - 1);

            // Eliminarlo del sistema
            getOperatingSystem().removeCPU(lastCpu);
            this.setNumCPUs(this.getNumCPUs() - 1);

            // Actualizar la UI
            this.updateCPUDisplays();

            JOptionPane.showMessageDialog(this, "CPU " + cpuCount + " eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addProcessToSimulation(String name, int instructions, boolean isIOBound, int exceptionThreshold, int exceptionSolve) {
        OurProcess newProcess = OperatingSystem.getInstance().addNewProcess(name, instructions, isIOBound, exceptionThreshold, exceptionSolve);

        if (newProcess != null) {
            JOptionPane.showMessageDialog(this, "Proceso '" + name + "' creado con exito!", "Proceso Creado", JOptionPane.INFORMATION_MESSAGE);
            this.numProcesses++;
            this.updateProcessTable();
            this.createProcessDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear el proceso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodos para actualizar los valores de las tablas y lista de cpus
    private void initListAndTable(int numCPUs, int numProcesses) {
        this.cpuListModel.clear();
        // Crea los cpus y procesos pero no inicia la simulacion
        getOperatingSystem().initCpuforUI(numCPUs);
        getOperatingSystem().initProcessforUI(numProcesses);
    }

    public void updateCPUList() {
        SimpleList<OurCPU> cpuList = this.getOperatingSystem().getCpuList();

        this.cpuListModel.clear();

        // Actualizar cada elemento de CPU segun el indice en la lista de cpus
        if (cpuList.getSize() >= 1) {
            cpuListModel.addElement("CPU 1");
        }
        if (cpuList.getSize() >= 2) {
            cpuListModel.addElement("CPU 2");
        }
        if (cpuList.getSize() >= 3) {
            cpuListModel.addElement("CPU 3");
        }

        this.cpusJList.setModel(cpuListModel);
    }

    private void updateProcessTable() {
        processTableModel.setRowCount(0);
        SimpleList<OurProcess> processes = getQueueManager().getProcessTable().getEntriesList();

        for (int i = 0; i < processes.getSize(); i++) {
            OurProcess process = processes.getValueByIndex(i);
            if (process != null) {
                PCB pcb = process.getPcb();

                processTableModel.addRow(new Object[]{
                    pcb.getId(),
                    pcb.getName(),
                    pcb.getState(),
                    pcb.getTotalInstructions(),
                    pcb.isIsIOBound() ? "YES" : "NO",
                    pcb.getPC(),
                    pcb.getMAR(),
                    pcb.isIsIOBound() ? pcb.getExceptionCycleThreshold() : "N/A",
                    pcb.isIsIOBound() ? pcb.getExceptionSolveNumber() : "N/A"
                });
                this.processJTable.setModel(processTableModel);
            }

        }
    }

    // Metodo para actualizar los detalles de los cpu en ejecucion
    private void updateCPUDisplays() {
        SimpleList<OurCPU> cpuList = getOperatingSystem().getCpuList();
        int activeCPUs = cpuList.getSize(); // Obtener num de CPUs activos

        updateSingleCPUDisplay(cpu1Panel, (activeCPUs >= 1) ? cpuList.getValueByIndex(0) : null, "CPU 1");
        updateSingleCPUDisplay(cpu2Panel, (activeCPUs >= 2) ? cpuList.getValueByIndex(1) : null, "CPU 2");
        updateSingleCPUDisplay(cpu3Panel, (activeCPUs >= 3) ? cpuList.getValueByIndex(2) : null, "CPU 3");
    }

    private void updateSingleCPUDisplay(JPanel cpuPanel, OurCPU cpu, String title) {
        if (cpuPanel == null) {
            return;
        }

        cpuPanel.removeAll();
        cpuPanel.setLayout(new BorderLayout(5, 5));

        if (cpu == null) {
            // CPU fue eliminado, marcar inactivo
            cpuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2), title + " - Inactivo"));
            cpuPanel.setBackground(new Color(220, 220, 220));
            JLabel inactiveLabel = new JLabel("CPU no disponible", JLabel.CENTER);
            inactiveLabel.setForeground(Color.DARK_GRAY);
            cpuPanel.add(inactiveLabel, BorderLayout.CENTER);

        } else {
            // CPU sigue activo
            boolean isBusy = cpu.isIsBusy();
            cpuPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(isBusy ? new Color(0, 150, 0) : Color.GRAY, 2),
                    title + (isBusy ? " - Ocupado" : " - Libre")
            ));
            cpuPanel.setBackground(isBusy ? new Color(220, 255, 220) : new Color(240, 240, 240));

            JPanel processInfoPanel = new JPanel(new GridLayout(0, 1, 2, 2));
            processInfoPanel.setOpaque(false);

            if (cpu.getCurrentProcess() != null) {
                PCB currentPCB = cpu.getCurrentProcess().getPcb();

                processInfoPanel.add(createInfoLabel("ID: " + currentPCB.getId()));
                processInfoPanel.add(createInfoLabel("Proceso: " + currentPCB.getName()));
                processInfoPanel.add(createInfoLabel("PC: " + currentPCB.getPC()));
                processInfoPanel.add(createInfoLabel("Estado: " + currentPCB.getState()));

                JProgressBar progressBar = new JProgressBar(0, currentPCB.getTotalInstructions());
                progressBar.setValue(currentPCB.getPC());
                progressBar.setStringPainted(true);
                progressBar.setString(currentPCB.getPC() + "/" + currentPCB.getTotalInstructions());

                JPanel progressPanel = new JPanel(new BorderLayout());
                progressPanel.setOpaque(false);
                progressPanel.add(new JLabel("Progreso: "), BorderLayout.WEST);
                progressPanel.add(progressBar, BorderLayout.CENTER);

                cpuPanel.add(processInfoPanel, BorderLayout.CENTER);
                cpuPanel.add(progressPanel, BorderLayout.SOUTH);
            } else {
                JLabel noProcessLabel = new JLabel("Sin proceso en ejecución", JLabel.CENTER);
                noProcessLabel.setForeground(Color.GRAY);
                cpuPanel.add(noProcessLabel, BorderLayout.CENTER);
            }
        }

        cpuPanel.revalidate();
        cpuPanel.repaint();
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    private void initQueuePanels() {
        // Configurar los paneles existentes
        this.setupQueuePanel(queueReadyPanel, "Cola de Listos");
        this.setupQueuePanel(queueBlockedPanel, "Cola de Bloqueados");
        this.setupQueuePanel(listFinishedPanel, "Lista de Terminados");
    }

    private void setupQueuePanel(JPanel panel, String title) {
        // Limpiar el panel existente
        panel.removeAll();
        panel.setLayout(new BorderLayout());

//        // Configurar título
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setHorizontalAlignment(JLabel.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
//        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        // Panel para los PCBs
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        // Scroll pane para el contenido
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Agregar componentes
//        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Guardar el contentPanel como propiedad del panel principal usando putClientProperty
        panel.putClientProperty("contentPanel", contentPanel);
    }

    private void updatePanelContent(JPanel panel, OurQueue<PCB> queue, ProcessState expectedState) {

        JPanel contentPanel = (JPanel) panel.getClientProperty("contentPanel");
        if (contentPanel != null) {
            contentPanel.removeAll();

            SimpleNode<PCB> currentNode = queue.getpFirst();
            while (currentNode != null) {
                PCB pcb = currentNode.getData();

                // Solo agregar si el proceso esta en el estado correcto
                if (pcb.getState() == expectedState) {
                    contentPanel.add(createPCBLabel(pcb));
                }

                currentNode = currentNode.getpNext();
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void updatePanelContentList(JPanel panel, SimpleList<PCB> list, ProcessState expectedState) {

        JPanel contentPanel = (JPanel) panel.getClientProperty("contentPanel");
        if (contentPanel != null) {
            contentPanel.removeAll();

            SimpleNode<PCB> currentNode = list.getpFirst();

            while (currentNode != null) {
                PCB pcb = currentNode.getData();

                if (pcb.getState() == expectedState) {
                    contentPanel.add(createPCBLabel(pcb));
                }

                currentNode = currentNode.getpNext();
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    // Incluye un mouse listener en cada label generado
    private JLabel createPCBLabel(PCB pcb) {
        JLabel label = new JLabel(pcb.getName());
        label.setOpaque(true);
        label.setBackground(this.getColorForState(pcb.getState()));
        label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(3, 6, 3, 6)));

        // ADD LISTENER
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                showProcessDetails(pcb);
            }
        });
        return label;
    }

    private Color getColorForState(ProcessState state) {
        switch (state) {
            case READY:
                return new Color(200, 230, 200); // Verde claro
            case BLOCKED:
                return new Color(230, 200, 200); // Rojo claro
            case FINISHED:
                return new Color(200, 200, 230); // Azul claro
            default:
                return Color.WHITE;
        }
    }

    private void showProcessDetails(PCB process) {
        processDetailsPanel.removeAll();
        //processDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2), "Detalles del Proceso", TitledBorder.LEADING, TitledBorder.TOP, new Font("Yu Gothic UI Semibold", Font.BOLD, 14)));

        processDetailsPanel.add(createDetailLabel("ID: " + process.getId()));
        processDetailsPanel.add(createDetailLabel("Nombre: " + process.getName()));
        processDetailsPanel.add(createDetailLabel("Estado: " + process.getState()));
        processDetailsPanel.add(createDetailLabel("PC: " + process.getPC()));
        processDetailsPanel.add(createDetailLabel("MAR: " + process.getMAR()));

        if (process.isIsIOBound()) {
            processDetailsPanel.add(createDetailLabel("Ciclos para I/O: " + process.getExceptionCycleThreshold()));
            processDetailsPanel.add(createDetailLabel("Ciclos para completar I/O: " + process.getExceptionSolveNumber()));
        }

        processDetailsPanel.revalidate();
        processDetailsPanel.repaint();
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        return label;
    }

// Metodo para actualizar todos los paneles
    public void updateQueueDisplays() {

        // Adquirir semáforos
        //queueManager.getReadyQueueSemaphore().acquire();
        //queueManager.getBlockedQueueSemaphore().acquire();
        //queueManager.getFinishedQueueSemaphore().acquire();
        // Actualizar paneles (+ CPUS)
        this.updatePanelContent(queueReadyPanel, getQueueManager().getReadyQueue(), ProcessState.READY);
        this.updatePanelContent(queueBlockedPanel, getQueueManager().getBlockedQueue(), ProcessState.BLOCKED);
        this.updatePanelContentList(listFinishedPanel, getQueueManager().getFinishedProcesses(), ProcessState.FINISHED);
        this.updateCPUDisplays();
        this.updateProcessTable();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configDialog = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        numProcessesField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        numCPUsField = new javax.swing.JTextField();
        cycleDurationField = new javax.swing.JTextField();
        acceptDialog = new javax.swing.JButton();
        cancelDialogParams = new javax.swing.JButton();
        startSimulationDialog = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        fcfsButton = new javax.swing.JButton();
        rrButton = new javax.swing.JButton();
        spnButton = new javax.swing.JButton();
        srtButton = new javax.swing.JButton();
        hrrnButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        cancelDialogSimulation = new javax.swing.JButton();
        createProcessDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        ioBoundCheckBox = new javax.swing.JCheckBox();
        generateRandomProcessField = new javax.swing.JTextField();
        exceptionSolveField = new javax.swing.JTextField();
        exceptionField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        instructionsField = new javax.swing.JTextField();
        generateRPButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        createPDButton = new javax.swing.JButton();
        cancelPDButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        createButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        createProcessButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        cycleDurationSlider = new javax.swing.JSlider();
        mainSimulationPanel = new javax.swing.JPanel();
        processDetailsPanel = new javax.swing.JPanel();
        cpusPanel = new javax.swing.JPanel();
        scrollCpusPane = new javax.swing.JScrollPane();
        cpusJList = new javax.swing.JList<>();
        createCpusButton = new javax.swing.JButton();
        deleteCpusButton = new javax.swing.JButton();
        simulationPanel = new javax.swing.JPanel();
        cpu1Panel = new javax.swing.JPanel();
        cpu2Panel = new javax.swing.JPanel();
        cpu3Panel = new javax.swing.JPanel();
        listFinishedPanel = new javax.swing.JPanel();
        queueReadyPanel = new javax.swing.JPanel();
        queueBlockedPanel = new javax.swing.JPanel();
        clockTickCycleLabel = new javax.swing.JLabel();
        processTablePanel = new javax.swing.JPanel();
        processTable = new javax.swing.JScrollPane();
        processJTable = new javax.swing.JTable();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveDataMenuItem = new javax.swing.JMenuItem();
        writeDataMenuItem = new javax.swing.JMenuItem();
        simulationOptions = new javax.swing.JMenu();
        changeSchedulerMenuItem = new javax.swing.JMenu();
        fcfsMenuItem = new javax.swing.JMenuItem();
        rrMenuItem = new javax.swing.JMenuItem();
        spnMenuItem = new javax.swing.JMenuItem();
        srtMenuItem = new javax.swing.JMenuItem();
        hrrnMenuItem = new javax.swing.JMenuItem();

        configDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parámetros"));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        numProcessesField.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        numProcessesField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        numProcessesField.setText("1");
        jPanel4.add(numProcessesField, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 140, 60, -1));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel3.setText("Número de procesos a generar:");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel4.setText("Número de CPUs:");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel5.setText("Duración del Ciclo (ms):");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        numCPUsField.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        numCPUsField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        numCPUsField.setText("2");
        jPanel4.add(numCPUsField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 60, -1));

        cycleDurationField.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        cycleDurationField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        cycleDurationField.setText("500");
        jPanel4.add(cycleDurationField, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 60, -1));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 340, 210));

        acceptDialog.setText("Aceptar");
        acceptDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptDialogActionPerformed(evt);
            }
        });
        jPanel3.add(acceptDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 270, -1, -1));

        cancelDialogParams.setText("Cancelar");
        cancelDialogParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelDialogParamsActionPerformed(evt);
            }
        });
        jPanel3.add(cancelDialogParams, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 270, -1, -1));

        configDialog.getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 340));

        startSimulationDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Planificadores"));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel8.setText("Seleccione el Algoritmo de Planificación deseado...");
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, -1, -1));

        fcfsButton.setText("FCFS");
        fcfsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fcfsButtonActionPerformed(evt);
            }
        });
        jPanel6.add(fcfsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        rrButton.setText("Round Robin");
        rrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rrButtonActionPerformed(evt);
            }
        });
        jPanel6.add(rrButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, -1, -1));

        spnButton.setText("SPN");
        spnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spnButtonActionPerformed(evt);
            }
        });
        jPanel6.add(spnButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, -1, -1));

        srtButton.setText("SRT");
        srtButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                srtButtonActionPerformed(evt);
            }
        });
        jPanel6.add(srtButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, -1, -1));

        hrrnButton.setText("HRRN");
        hrrnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hrrnButtonActionPerformed(evt);
            }
        });
        jPanel6.add(hrrnButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 160, -1, -1));

        jButton1.setText("FeedBack");
        jPanel6.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, -1, -1));

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 600, 210));

        cancelDialogSimulation.setText("Cancelar");
        cancelDialogSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelDialogSimulationActionPerformed(evt);
            }
        });
        jPanel5.add(cancelDialogSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 240, -1, -1));

        startSimulationDialog.getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        createProcessDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Crear un Proceso"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ioBoundCheckBox.setText("I/O Bound?");
        ioBoundCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ioBoundCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(ioBoundCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, 23));
        jPanel1.add(generateRandomProcessField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 60, 23));

        exceptionSolveField.setEnabled(false);
        jPanel1.add(exceptionSolveField, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 180, 83, 23));

        exceptionField.setEnabled(false);
        jPanel1.add(exceptionField, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 110, 83, 23));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel1.setText("a generar");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel2.setText("Instrucciones Totales:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel6.setText("Ciclos para generar excepción:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, -1, -1));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel7.setText("Ciclos para resolver excepción:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 150, -1, -1));
        jPanel1.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 120, 23));
        jPanel1.add(instructionsField, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 60, 23));

        generateRPButton.setText("Generar Procesos");
        generateRPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateRPButtonActionPerformed(evt);
            }
        });
        jPanel1.add(generateRPButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, -1, -1));

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel9.setText("Nombre del proceso:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jLabel10.setText("Numero de procesos");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        createProcessDialog.getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 550, 260));

        createPDButton.setText("Crear");
        createPDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createPDButtonActionPerformed(evt);
            }
        });
        createProcessDialog.getContentPane().add(createPDButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 270, 83, -1));

        cancelPDButton.setText("Cancelar");
        cancelPDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelPDButtonActionPerformed(evt);
            }
        });
        createProcessDialog.getContentPane().add(cancelPDButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 270, 100, -1));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        controlPanel.setBackground(new java.awt.Color(204, 255, 204));

        createButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        createButton.setText("Crear");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createButton);

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator1);

        jSeparator3.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator3);

        startButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        startButton.setText("Iniciar");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        controlPanel.add(startButton);

        stopButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        stopButton.setText("Detener");
        stopButton.setEnabled(false);
        controlPanel.add(stopButton);

        jSeparator4.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator4.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator4);

        jSeparator5.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator5.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator5);

        createProcessButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        createProcessButton.setText("(+) Proceso");
        createProcessButton.setEnabled(false);
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createProcessButton);

        jSeparator6.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator6.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator6);

        jSeparator7.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator7.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator7);

        cycleDurationSlider.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        cycleDurationSlider.setMajorTickSpacing(1000);
        cycleDurationSlider.setMaximum(5000);
        cycleDurationSlider.setMinimum(500);
        cycleDurationSlider.setMinorTickSpacing(500);
        cycleDurationSlider.setPaintLabels(true);
        cycleDurationSlider.setPaintTicks(true);
        cycleDurationSlider.setSnapToTicks(true);
        cycleDurationSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cycleDurationSlider.setEnabled(false);
        cycleDurationSlider.setOpaque(true);
        cycleDurationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cycleDurationSliderStateChanged(evt);
            }
        });
        controlPanel.add(cycleDurationSlider);

        getContentPane().add(controlPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 50));

        mainSimulationPanel.setBackground(new java.awt.Color(153, 0, 153));
        mainSimulationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        processDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles del Proceso:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 14))); // NOI18N

        javax.swing.GroupLayout processDetailsPanelLayout = new javax.swing.GroupLayout(processDetailsPanel);
        processDetailsPanel.setLayout(processDetailsPanelLayout);
        processDetailsPanelLayout.setHorizontalGroup(
            processDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );
        processDetailsPanelLayout.setVerticalGroup(
            processDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );

        mainSimulationPanel.add(processDetailsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 270, 310));

        cpusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("CPU Control"));

        scrollCpusPane.setViewportView(cpusJList);

        cpusPanel.add(scrollCpusPane);

        createCpusButton.setText("Añadir");
        createCpusButton.setEnabled(false);
        createCpusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCpusButtonActionPerformed(evt);
            }
        });
        cpusPanel.add(createCpusButton);

        deleteCpusButton.setText("Eliminar");
        deleteCpusButton.setEnabled(false);
        deleteCpusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCpusButtonActionPerformed(evt);
            }
        });
        cpusPanel.add(deleteCpusButton);

        mainSimulationPanel.add(cpusPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 270, 260));

        simulationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulacion"));
        simulationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cpu1Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU-X", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout cpu1PanelLayout = new javax.swing.GroupLayout(cpu1Panel);
        cpu1Panel.setLayout(cpu1PanelLayout);
        cpu1PanelLayout.setHorizontalGroup(
            cpu1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        cpu1PanelLayout.setVerticalGroup(
            cpu1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 97, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu1Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 150, 120));

        cpu2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU-X", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout cpu2PanelLayout = new javax.swing.GroupLayout(cpu2Panel);
        cpu2Panel.setLayout(cpu2PanelLayout);
        cpu2PanelLayout.setHorizontalGroup(
            cpu2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        cpu2PanelLayout.setVerticalGroup(
            cpu2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 97, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu2Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 150, 120));

        cpu3Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU-X", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout cpu3PanelLayout = new javax.swing.GroupLayout(cpu3Panel);
        cpu3Panel.setLayout(cpu3PanelLayout);
        cpu3PanelLayout.setHorizontalGroup(
            cpu3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        cpu3PanelLayout.setVerticalGroup(
            cpu3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 97, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu3Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 150, 120));

        listFinishedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE TERMINADOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout listFinishedPanelLayout = new javax.swing.GroupLayout(listFinishedPanel);
        listFinishedPanel.setLayout(listFinishedPanelLayout);
        listFinishedPanelLayout.setHorizontalGroup(
            listFinishedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );
        listFinishedPanelLayout.setVerticalGroup(
            listFinishedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(listFinishedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 200, 220, 70));

        queueReadyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "COLA DE LISTOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout queueReadyPanelLayout = new javax.swing.GroupLayout(queueReadyPanel);
        queueReadyPanel.setLayout(queueReadyPanelLayout);
        queueReadyPanelLayout.setHorizontalGroup(
            queueReadyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );
        queueReadyPanelLayout.setVerticalGroup(
            queueReadyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(queueReadyPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 220, 70));

        queueBlockedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "COLA DE BLOQUEADOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout queueBlockedPanelLayout = new javax.swing.GroupLayout(queueBlockedPanel);
        queueBlockedPanel.setLayout(queueBlockedPanelLayout);
        queueBlockedPanelLayout.setHorizontalGroup(
            queueBlockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );
        queueBlockedPanelLayout.setVerticalGroup(
            queueBlockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(queueBlockedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, 220, 70));

        clockTickCycleLabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 14)); // NOI18N
        clockTickCycleLabel.setText("Ciclo Global de Reloj: ");
        simulationPanel.add(clockTickCycleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, 270, 20));

        mainSimulationPanel.add(simulationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 980, 310));

        processTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tabla de Procesos"));
        processTablePanel.setLayout(new java.awt.BorderLayout());

        processJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Estado", "Total de Instrucciones", "I/O Bound", "PC", "MAR", "Ciclos para I/O", "Ciclos para completar I/O"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        processTable.setViewportView(processJTable);

        processTablePanel.add(processTable, java.awt.BorderLayout.CENTER);

        mainSimulationPanel.add(processTablePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 340, 980, 260));

        getContentPane().add(mainSimulationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1370, 700));

        fileMenu.setText("Archivo");

        saveDataMenuItem.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        saveDataMenuItem.setText("Guardar Cambios");
        saveDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveDataMenuItem);

        writeDataMenuItem.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        writeDataMenuItem.setText("Cargar Datos");
        writeDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeDataMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(writeDataMenuItem);

        mainMenuBar.add(fileMenu);

        simulationOptions.setText("Simulación");

        changeSchedulerMenuItem.setText("Cambiar Planificador");
        changeSchedulerMenuItem.setEnabled(false);

        fcfsMenuItem.setText("FCFS");
        changeSchedulerMenuItem.add(fcfsMenuItem);

        rrMenuItem.setText("Round Robin");
        changeSchedulerMenuItem.add(rrMenuItem);

        spnMenuItem.setText("SPN");
        changeSchedulerMenuItem.add(spnMenuItem);

        srtMenuItem.setText("SRT");
        changeSchedulerMenuItem.add(srtMenuItem);

        hrrnMenuItem.setText("HRRN");
        changeSchedulerMenuItem.add(hrrnMenuItem);

        simulationOptions.add(changeSchedulerMenuItem);

        mainMenuBar.add(simulationOptions);

        setJMenuBar(mainMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // TODO add your handling code here:
        if (this.confirmed) {
            JOptionPane.showMessageDialog(this, "Ya creó anteriormente su configuración inicial...", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.showConfigDialog(this);
        }

    }//GEN-LAST:event_createButtonActionPerformed

    private void cancelDialogParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelDialogParamsActionPerformed
        // TODO add your handling code here:
        this.confirmed = false;
        this.configDialog.dispose();
    }//GEN-LAST:event_cancelDialogParamsActionPerformed

    private void acceptDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptDialogActionPerformed
        // TODO add your handling code here:

        // obtain values
        try {
            int cycle = Integer.parseInt(cycleDurationField.getText());
            int cpu = Integer.parseInt(numCPUsField.getText());
            int process = Integer.parseInt(numProcessesField.getText());

            if (cycle < 500 || cpu < 1 || cpu > 3 || process < 1) {
                throw new NumberFormatException();
            }

            this.setCycleDuration(cycle);
            this.setNumCPUs(cpu);
            this.setNumProcesses(process);
            confirmed = true;
            // Cambiar la duracion del ciclo y permisos del reloj (esto ultimo depende del num de cpus)
            getOperatingSystem().getSystemClock().setCycleDuration(getCycleDuration());
            getOperatingSystem().getSystemClock().setPermissionsRequired(getNumCPUs());
            // valor para el slider en ui
            this.cycleDurationSlider.setValue(getCycleDuration());

            // iniciar los valores de cpus y procesos y mostrarlos en los jpanel correspondientes
            this.initListAndTable(getNumCPUs(), getNumProcesses());
            this.updateProcessTable();

            System.out.println("DURACION: " + this.getCycleDuration() + "\nCPUS: " + this.getNumCPUs() + "\nPROCESOS: " + this.getNumProcesses());
            this.configDialog.dispose();
            // Cambiar estado de botones
            this.createProcessButton.setEnabled(true);
            this.cycleDurationSlider.setEnabled(true);

            this.createButton.setEnabled(false);
            this.createCpusButton.setEnabled(true);
            this.deleteCpusButton.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.configDialog, "Por favor, ingrese valores numéricos válidos.\n- Duración del ciclo debe ser >= 500 ms\n- Número de CPUs debe ser 1<= x <= 3\n- Número de procesos debe ser >= 1", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_acceptDialogActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        if (this.confirmed && this.getQueueManager().getReadyQueueSize() != 0 && this.getOperatingSystem().getCpuList() != null) {

            this.showStartSimulationDialog(this);

        } else {
            JOptionPane.showMessageDialog(this, "Por favor, primero debe crear los elementos principales en la seccion de 'CREAR' o 'CARGAR' antes de poder iniciar la simulación", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void cancelDialogSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelDialogSimulationActionPerformed
        // TODO add your handling code here:
        this.startSimulation = false;
        System.out.println(" * Rejected startup...");
        this.startSimulationDialog.dispose();
    }//GEN-LAST:event_cancelDialogSimulationActionPerformed

    private void fcfsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fcfsButtonActionPerformed
        // TODO add your handling code here:
        // Algoritmo escogido
        int choice = 1;
        this.gottaStartSimulation(choice);
    }//GEN-LAST:event_fcfsButtonActionPerformed

    private void createCpusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCpusButtonActionPerformed
        // TODO add your handling code here:
        if (this.getOperatingSystem().getCpuList().getSize() >= 3) {
            JOptionPane.showMessageDialog(this, "No se pueden añadir más de 3 CPUs.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llamar para añadir un nuevo CPU
        this.addCPUToSimulation();

    }//GEN-LAST:event_createCpusButtonActionPerformed

    private void deleteCpusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCpusButtonActionPerformed
        // TODO add your handling code here:
        if (this.getOperatingSystem().getCpuList().getSize() > 1) {
            this.removeCPUFromSimulation();
            this.updateCPUList();
        } else {
            JOptionPane.showMessageDialog(this, "Ya no se pueden eliminar más CPUs.", "Límite de CPUs", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_deleteCpusButtonActionPerformed

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProcessButtonActionPerformed
        // TODO add your handling code here:
        if (this.getOperatingSystem().getCpuList() == null || this.getOperatingSystem().getCpuList().getSize() == 0) {
            JOptionPane.showMessageDialog(this, "Error, todavía no se han creado CPUs en el sistema!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.showCreateProcessDialog(this);

    }//GEN-LAST:event_createProcessButtonActionPerformed

    private void cycleDurationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cycleDurationSliderStateChanged
        // TODO add your handling code here:
        int newSpeed = this.cycleDurationSlider.getValue();
        System.out.println(newSpeed);
        getOperatingSystem().getSystemClock().setCycleDuration(newSpeed);
    }//GEN-LAST:event_cycleDurationSliderStateChanged

    private void createPDButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createPDButtonActionPerformed
        // TODO add your handling code here:
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proceso no puede estar vacio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int instructions;
        try {
            instructions = Integer.parseInt(instructionsField.getText().trim());
            if (instructions <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero válido de instrucciones.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isIOBound = ioBoundCheckBox.isSelected();
        int exceptionThreshold = 0;
        int exceptionSolve = 0;

        if (isIOBound) {
            try {
                exceptionThreshold = Integer.parseInt(exceptionField.getText().trim());
                exceptionSolve = Integer.parseInt(exceptionSolveField.getText().trim());

                if (exceptionThreshold <= 0 || exceptionSolve <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese valores válidos para los ciclos de excepción.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        this.addProcessToSimulation(name, instructions, isIOBound, exceptionThreshold, exceptionSolve);

    }//GEN-LAST:event_createPDButtonActionPerformed

    private void cancelPDButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelPDButtonActionPerformed
        // TODO add your handling code here:
        this.createProcessDialog.dispose();
    }//GEN-LAST:event_cancelPDButtonActionPerformed

    private void ioBoundCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ioBoundCheckBoxActionPerformed
        // TODO add your handling code here:
        if (ioBoundCheckBox.isSelected()) {
            exceptionField.setEnabled(true);
            exceptionSolveField.setEnabled(true);
        } else {
            exceptionField.setEnabled(false);
            exceptionSolveField.setEnabled(false);
        }


    }//GEN-LAST:event_ioBoundCheckBoxActionPerformed

    private void generateRPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateRPButtonActionPerformed
        // TODO add your handling code here:

        // obtain new num of processes values
        try {
            int numOfProcesses = Integer.parseInt(generateRandomProcessField.getText());

            if (numOfProcesses < 1) {
                throw new NumberFormatException();
            }

            for (int i = 0; i < numOfProcesses; i++) {
                System.out.println("Intentando crear proceso #" + (i + 1));
                this.getOperatingSystem().generateNewProcess();
            }

            this.numProcesses = this.queueManager.getProcessTable().getEntriesList().getSize() + numOfProcesses;

            // Actualizar la tabla de procesos en la UI
            this.updateProcessTable();
            this.createProcessDialog.dispose();
            JOptionPane.showMessageDialog(this, numOfProcesses + " procesos creados con éxito!", "Procesos Creado", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.configDialog, "Por favor, ingrese valores numéricos válidos.\n- Duración del ciclo debe ser >= 500 ms\n- Número de CPUs debe ser 1<= x <= 3\n- Número de procesos debe ser >= 1", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }//GEN-LAST:event_generateRPButtonActionPerformed

    private void saveDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDataMenuItemActionPerformed
        // TODO add your handling code here:
        if (this.confirmed && this.getQueueManager().getReadyQueueSize() != 0 && this.getOperatingSystem().getCpuList() != null) {

            // Guardar configuracion deseada antes de la simulacion
            fileManager.saveSimulationConfig(this.getCycleDuration(), this.getNumCPUs(), this.getNumProcesses());
            fileManager.saveProcessData(this.getQueueManager().getProcessTable(), this.getNumCPUs());
            JOptionPane.showMessageDialog(this, "Configuración guardada en la raíz del proyecto!", "Configuration Savedata", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Debe crear los elementos principales en la seccion de 'CREAR' o 'CARGAR' antes de poder reescribir datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_saveDataMenuItemActionPerformed

    private void rrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rrButtonActionPerformed
        // TODO add your handling code here:
        // Algoritmo escogido
        int choice = 2;
        this.gottaStartSimulation(choice);

    }//GEN-LAST:event_rrButtonActionPerformed

    private void spnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spnButtonActionPerformed
        // TODO add your handling code here:
        // Algoritmo escogido
        int choice = 3;
        this.gottaStartSimulation(choice);
    }//GEN-LAST:event_spnButtonActionPerformed

    private void srtButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_srtButtonActionPerformed
        // TODO add your handling code here:
        // Algoritmo escogido
        int choice = 4;
        this.gottaStartSimulation(choice);
    }//GEN-LAST:event_srtButtonActionPerformed

    private void hrrnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hrrnButtonActionPerformed
        // TODO add your handling code here:
        // Algoritmo escogido
        int choice = 5;
        this.gottaStartSimulation(choice);
    }//GEN-LAST:event_hrrnButtonActionPerformed

    private void writeDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeDataMenuItemActionPerformed
        // TODO add your handling code here:
        // CARGAR CONFIG DESDE TXT Y JSON
        fileManager.loadSimulationData(this, this.getQueueManager().getProcessTable());
        // Actualizar valores de interfaz
        this.updateProcessTable();
        System.out.println("DURACION: " + this.getCycleDuration() + "\nCPUS: " + this.getNumCPUs() + "\nPROCESOS: " + this.getNumProcesses());

        // Cambiar estado
        this.confirmed = true;
        this.createProcessButton.setEnabled(true);
        this.cycleDurationSlider.setEnabled(true);

        this.createButton.setEnabled(false);
        this.createCpusButton.setEnabled(true);
        this.deleteCpusButton.setEnabled(true);

    }//GEN-LAST:event_writeDataMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimulationUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptDialog;
    private javax.swing.JButton cancelDialogParams;
    private javax.swing.JButton cancelDialogSimulation;
    private javax.swing.JButton cancelPDButton;
    private javax.swing.JMenu changeSchedulerMenuItem;
    private javax.swing.JLabel clockTickCycleLabel;
    private javax.swing.JDialog configDialog;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel cpu1Panel;
    private javax.swing.JPanel cpu2Panel;
    private javax.swing.JPanel cpu3Panel;
    private javax.swing.JList<String> cpusJList;
    private javax.swing.JPanel cpusPanel;
    private javax.swing.JButton createButton;
    private javax.swing.JButton createCpusButton;
    private javax.swing.JButton createPDButton;
    private javax.swing.JButton createProcessButton;
    private javax.swing.JDialog createProcessDialog;
    private javax.swing.JTextField cycleDurationField;
    private javax.swing.JSlider cycleDurationSlider;
    private javax.swing.JButton deleteCpusButton;
    private javax.swing.JTextField exceptionField;
    private javax.swing.JTextField exceptionSolveField;
    private javax.swing.JButton fcfsButton;
    private javax.swing.JMenuItem fcfsMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton generateRPButton;
    private javax.swing.JTextField generateRandomProcessField;
    private javax.swing.JButton hrrnButton;
    private javax.swing.JMenuItem hrrnMenuItem;
    private javax.swing.JTextField instructionsField;
    private javax.swing.JCheckBox ioBoundCheckBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPanel listFinishedPanel;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JPanel mainSimulationPanel;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField numCPUsField;
    private javax.swing.JTextField numProcessesField;
    private javax.swing.JPanel processDetailsPanel;
    private javax.swing.JTable processJTable;
    private javax.swing.JScrollPane processTable;
    private javax.swing.JPanel processTablePanel;
    private javax.swing.JPanel queueBlockedPanel;
    private javax.swing.JPanel queueReadyPanel;
    private javax.swing.JButton rrButton;
    private javax.swing.JMenuItem rrMenuItem;
    private javax.swing.JMenuItem saveDataMenuItem;
    private javax.swing.JScrollPane scrollCpusPane;
    private javax.swing.JMenu simulationOptions;
    private javax.swing.JPanel simulationPanel;
    private javax.swing.JButton spnButton;
    private javax.swing.JMenuItem spnMenuItem;
    private javax.swing.JButton srtButton;
    private javax.swing.JMenuItem srtMenuItem;
    private javax.swing.JButton startButton;
    private javax.swing.JDialog startSimulationDialog;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenuItem writeDataMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the simulationUiInstance
     */
    public static SimulationUI getSimulationUiInstance() {
        return simulationUiInstance;
    }

    /**
     * @param aSimulationUiInstance the simulationUiInstance to set
     */
    public static void setSimulationUiInstance(SimulationUI aSimulationUiInstance) {
        simulationUiInstance = aSimulationUiInstance;
    }

    /**
     * @return the cycleDuration
     */
    public int getCycleDuration() {
        return cycleDuration;
    }

    /**
     * @return the numCPUs
     */
    public int getNumCPUs() {
        return numCPUs;
    }

    /**
     * @return the numProcesses
     */
    public int getNumProcesses() {
        return numProcesses;
    }

    /**
     * @return the cpuListModel
     */
    public DefaultListModel<String> getCpuListModel() {
        return cpuListModel;
    }

    /**
     * @return the cpusJList
     */
    public javax.swing.JList<String> getCpusJList() {
        return cpusJList;
    }

    /**
     * @return the clockTickCycleLabel
     */
    public javax.swing.JLabel getClockTickCycleLabel() {
        return clockTickCycleLabel;
    }

    /**
     * @param cycleDuration the cycleDuration to set
     */
    public void setCycleDuration(int cycleDuration) {
        this.cycleDuration = cycleDuration;
    }

    /**
     * @param numCPUs the numCPUs to set
     */
    public void setNumCPUs(int numCPUs) {
        this.numCPUs = numCPUs;
    }

    /**
     * @param numProcesses the numProcesses to set
     */
    public void setNumProcesses(int numProcesses) {
        this.numProcesses = numProcesses;
    }

    /**
     * @return the operatingSystem
     */
    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * @return the queueManager
     */
    public QueueManager getQueueManager() {
        return queueManager;
    }

}
