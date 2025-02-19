/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main.GUI;

import Classes.ProcessFactory.OurProcess;
import Classes.ProcessFactory.PCB;
import Classes.ProcessFactory.ProcessState;
import Classes.Scheduler.QueueManager;
import Classes.Scheduler.pFirstComeFirstServed;
import EDD.OurQueue;
import EDD.SimpleList;
import EDD.SimpleNode;
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

    /**
     * Creates new form Simulation
     */
    public SimulationUI() {
        initComponents();
        // gui properties
        this.setTitle("Planificador de Procesos");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setSize(1300, 775);
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

    private void showConfigDialog(Frame parent) {
        this.configDialog.setTitle("Configuración");
        this.configDialog.setSize(400, 350);
        this.configDialog.setLocationRelativeTo(parent);
        this.configDialog.setVisible(true);
    }

    private void showStartSimulationDialog(Frame parent) {
        this.startSimulationDialog.setTitle("Iniciar Simulación");
        this.startSimulationDialog.setSize(634, 318);
        this.startSimulationDialog.setLocationRelativeTo(parent);
        this.startSimulationDialog.setVisible(true);
    }

    private void addCPUToSimulation() {
        operatingSystem.addProcessor();
        this.updateCPUDisplays();
        JOptionPane.showMessageDialog(this, "Un CPU se ha creado con éxito!", "Añadir CPUs", JOptionPane.INFORMATION_MESSAGE);

        // Habilitar o deshabilitar el botón según la cantidad de CPUs
    }

    private void removeCPUFromSimulation() {
        int selectedIndex = cpusJList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un CPU para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el CPU seleccionado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener el CPU seleccionado
            OurCPU selectedCPU = operatingSystem.getCpuList().getValueByIndex(selectedIndex);

            operatingSystem.removeCPU(selectedCPU);

            // Actualizar la vista de los CPUs solo si hay restantes
            if (operatingSystem.getCpuList().getSize() > 0) {
                updateCPUDisplays();  // 
            }
        }
    }

    private void addNewProcessToSimulation() {

    }

    // Metodos para actualizar los valores de las tablas y lista de cpus
    private void initListAndTable(int numCPUs, int numProcesses) {
        this.cpuListModel.clear();
        // Crea los cpus y procesos pero no inicia la simulacion
        operatingSystem.initUISystemValues(numCPUs, numProcesses);
    }

    private void updateProcessTable() {
        processTableModel.setRowCount(0);
        SimpleList<OurProcess> processes = queueManager.getProcessTable().getEntriesList();

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

    private void updateCPUDisplays() {
        SimpleList<OurCPU> cpuList = operatingSystem.getCpuList();
        if (cpuList == null || cpuList.getSize() == 0) {
            cpuListModel.clear();
            return;
        }
        cpuListModel.clear();

        // Actualizar cada panel de CPU segun el indice en la lista de cpus
        if (cpuList.getSize() >= 1) {
            updateSingleCPUDisplay(cpu1Panel, cpuList.getValueByIndex(0), "CPU 1");
            cpuListModel.addElement("CPU 1 - " + (cpuList.getValueByIndex(0).isIsBusy() ? "Activo" : "Inactivo"));
        }
        if (cpuList.getSize() >= 2) {
            updateSingleCPUDisplay(cpu2Panel, cpuList.getValueByIndex(1), "CPU 2");
            cpuListModel.addElement("CPU 2 - " + (cpuList.getValueByIndex(1).isIsBusy() ? "Activo" : "Inactivo"));
        }
        if (cpuList.getSize() >= 3) {
            updateSingleCPUDisplay(cpu3Panel, cpuList.getValueByIndex(2), "CPU 3");
            cpuListModel.addElement("CPU 3 - " + (cpuList.getValueByIndex(2).isIsBusy() ? "Activo" : "Inactivo"));
        }
        cpusJList.setModel(cpuListModel);
    }

    private void updateSingleCPUDisplay(JPanel cpuPanel, OurCPU cpu, String title) {
        if (cpuPanel == null || cpu == null) {
            return;
        }

        // Configurar panel
        cpuPanel.removeAll();
        cpuPanel.setLayout(new BorderLayout(5, 5));

        // Crear un borde con título que indique el estado
        String statusText = cpu.isIsBusy() ? " - Activo" : " - Inactivo";

        cpuPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(cpu.isIsBusy() ? new Color(0, 150, 0) : Color.GRAY, 2),
                title + statusText
        ));

        // Establecer el color de fondo segun su estado
        cpuPanel.setBackground(cpu.isIsBusy() ? new Color(220, 255, 220) : new Color(240, 240, 240));

        // Nuevo panel para info detallada
        JPanel processInfoPanel = new JPanel();
        processInfoPanel.setLayout(new GridLayout(0, 1, 2, 2));
        processInfoPanel.setOpaque(false);

        if (cpu.getCurrentProcess() != null) {
            PCB currentPCB = cpu.getCurrentProcess().getPcb();

            processInfoPanel.add(createInfoLabel("ID: " + currentPCB.getId()));
            processInfoPanel.add(createInfoLabel("Proceso: " + currentPCB.getName()));
            processInfoPanel.add(createInfoLabel("PC: " + currentPCB.getPC()));
            processInfoPanel.add(createInfoLabel("Estado: " + currentPCB.getState()));

            // Agregar barra de progreso
            JProgressBar progressBar = new JProgressBar(0, currentPCB.getTotalInstructions());
            progressBar.setValue(currentPCB.getPC());
            progressBar.setStringPainted(true);
            progressBar.setString(currentPCB.getPC() + "/" + currentPCB.getTotalInstructions());

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setOpaque(false);
            progressPanel.add(new JLabel("Progreso: "), BorderLayout.WEST);
            progressPanel.add(progressBar, BorderLayout.CENTER);

            // Pal panel principal
            cpuPanel.add(processInfoPanel, BorderLayout.CENTER);
            cpuPanel.add(progressPanel, BorderLayout.SOUTH);
        } else {
            JLabel noProcessLabel = new JLabel("Sin proceso en ejecución");
            noProcessLabel.setHorizontalAlignment(JLabel.CENTER);
            noProcessLabel.setForeground(Color.GRAY);
            cpuPanel.add(noProcessLabel, BorderLayout.CENTER);
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

    private void updatePanelContent(JPanel panel, OurQueue<PCB> queue) {

        JPanel contentPanel = (JPanel) panel.getClientProperty("contentPanel");
        if (contentPanel != null) {
            contentPanel.removeAll();

            SimpleNode<PCB> currentNode = queue.getpFirst();
            while (currentNode != null) {
                PCB pcb = currentNode.getData();
                contentPanel.add(createPCBLabel(pcb));
                currentNode = currentNode.getpNext();
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void updatePanelContentList(JPanel panel, SimpleList<PCB> list) {

        JPanel contentPanel = (JPanel) panel.getClientProperty("contentPanel");
        if (contentPanel != null) {
            contentPanel.removeAll();

            SimpleNode<PCB> currentNode = list.getpFirst();
            while (currentNode != null) {
                PCB pcb = currentNode.getData();
                contentPanel.add(createPCBLabel(pcb));
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
        try {
            // Adquirir semáforos
            queueManager.getReadyQueueSemaphore().acquire();
            queueManager.getBlockedQueueSemaphore().acquire();
            queueManager.getFinishedQueueSemaphore().acquire();

            // Actualizar paneles (+ CPUS)
            this.updatePanelContent(queueReadyPanel, queueManager.getReadyQueue());
            this.updatePanelContent(queueBlockedPanel, queueManager.getBlockedQueue());
            this.updatePanelContentList(listFinishedPanel, queueManager.getFinishedProcesses());
            this.updateCPUDisplays();
            this.updateProcessTable();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Liberar semáforos
            queueManager.getReadyQueueSemaphore().release();
            queueManager.getBlockedQueueSemaphore().release();
            queueManager.getFinishedQueueSemaphore().release();
        }
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
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        cancelDialogSimulation = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        createButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        createProcessButton = new javax.swing.JButton();
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
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        simulationOptions = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        fcfsMenuItem = new javax.swing.JMenuItem();

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

        configDialog.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

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

        jButton2.setText("Round Robin");
        jPanel6.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, -1, -1));

        jButton3.setText("SPN");
        jPanel6.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, -1, -1));

        jButton5.setText("SRT");
        jPanel6.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, -1, -1));

        jButton4.setText("HRRN");
        jPanel6.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 160, -1, -1));

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

        startSimulationDialog.getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

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

        loadButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        loadButton.setText("Cargar");
        controlPanel.add(loadButton);

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        controlPanel.add(jSeparator1);

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
        controlPanel.add(jSeparator2);

        createProcessButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        createProcessButton.setText("(+) Proceso");
        createProcessButton.setEnabled(false);
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createProcessButton);

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

        getContentPane().add(controlPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 50));

        mainSimulationPanel.setBackground(new java.awt.Color(153, 0, 153));
        mainSimulationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        processDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles del Proceso:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 14))); // NOI18N

        javax.swing.GroupLayout processDetailsPanelLayout = new javax.swing.GroupLayout(processDetailsPanel);
        processDetailsPanel.setLayout(processDetailsPanelLayout);
        processDetailsPanelLayout.setHorizontalGroup(
            processDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        processDetailsPanelLayout.setVerticalGroup(
            processDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 433, Short.MAX_VALUE)
        );

        mainSimulationPanel.add(processDetailsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 270, 460));

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

        mainSimulationPanel.add(cpusPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 500, 270, 220));

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
            .addGap(0, 117, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu1Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 150, 140));

        cpu2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU-X", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout cpu2PanelLayout = new javax.swing.GroupLayout(cpu2Panel);
        cpu2Panel.setLayout(cpu2PanelLayout);
        cpu2PanelLayout.setHorizontalGroup(
            cpu2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        cpu2PanelLayout.setVerticalGroup(
            cpu2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu2Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, 150, 140));

        cpu3Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CPU-X", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout cpu3PanelLayout = new javax.swing.GroupLayout(cpu3Panel);
        cpu3Panel.setLayout(cpu3PanelLayout);
        cpu3PanelLayout.setHorizontalGroup(
            cpu3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        cpu3PanelLayout.setVerticalGroup(
            cpu3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
        );

        simulationPanel.add(cpu3Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 370, 150, 140));

        listFinishedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE TERMINADOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout listFinishedPanelLayout = new javax.swing.GroupLayout(listFinishedPanel);
        listFinishedPanel.setLayout(listFinishedPanelLayout);
        listFinishedPanelLayout.setHorizontalGroup(
            listFinishedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        listFinishedPanelLayout.setVerticalGroup(
            listFinishedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(listFinishedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 300, 510, 70));

        queueReadyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "COLA DE LISTOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout queueReadyPanelLayout = new javax.swing.GroupLayout(queueReadyPanel);
        queueReadyPanel.setLayout(queueReadyPanelLayout);
        queueReadyPanelLayout.setHorizontalGroup(
            queueReadyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        queueReadyPanelLayout.setVerticalGroup(
            queueReadyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(queueReadyPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, 510, 70));

        queueBlockedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "COLA DE BLOQUEADOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 12))); // NOI18N

        javax.swing.GroupLayout queueBlockedPanelLayout = new javax.swing.GroupLayout(queueBlockedPanel);
        queueBlockedPanel.setLayout(queueBlockedPanelLayout);
        queueBlockedPanelLayout.setHorizontalGroup(
            queueBlockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        queueBlockedPanelLayout.setVerticalGroup(
            queueBlockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        simulationPanel.add(queueBlockedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 200, 510, 70));

        clockTickCycleLabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 14)); // NOI18N
        clockTickCycleLabel.setText("Ciclo Global de Reloj: ");
        simulationPanel.add(clockTickCycleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 270, 20));

        mainSimulationPanel.add(simulationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 980, 530));

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

        mainSimulationPanel.add(processTablePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 560, 980, 190));

        getContentPane().add(mainSimulationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1300, 760));

        fileMenu.setText("Archivo");
        jMenuBar1.add(fileMenu);

        simulationOptions.setText("Simulación");

        jMenu3.setText("PLANIFICACIÓN");

        fcfsMenuItem.setText("FCFS");
        jMenu3.add(fcfsMenuItem);

        simulationOptions.add(jMenu3);

        jMenuBar1.add(simulationOptions);

        setJMenuBar(jMenuBar1);

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

            this.cycleDuration = cycle;
            this.numCPUs = cpu;
            this.numProcesses = process;
            confirmed = true;
            // Cambiar la duracion del ciclo y permisos del reloj (esto ultimo depende del num de cpus)
            operatingSystem.getSystemClock().setCycleDuration(cycleDuration);
            operatingSystem.getSystemClock().setPermissionsRequired(numCPUs);
            // valor para el slider en ui
            this.cycleDurationSlider.setValue(cycleDuration);

            // iniciar los valores de cpus y procesos y mostrarlos en los jpanel correspondientes
            this.initListAndTable(numCPUs, numProcesses);
            this.updateProcessTable();

            System.out.println("DURACION: " + this.getCycleDuration() + "\nCPUS: " + this.getNumCPUs() + "\nPROCESOS: " + this.getNumProcesses());
            this.configDialog.dispose();
            // Cambiar estado de botones
            this.createCpusButton.setEnabled(true);
            this.deleteCpusButton.setEnabled(true);
            this.createProcessButton.setEnabled(true);
            this.cycleDurationSlider.setEnabled(true);

            // Establecer boton inicial apagado
            this.createButton.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.configDialog, "Por favor, ingrese valores numéricos válidos.\n- Duración del ciclo debe ser >= 500 ms\n- Número de CPUs debe ser 1<= x <= 3\n- Número de procesos debe ser >= 1", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_acceptDialogActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        if (this.confirmed && this.queueManager.getReadyQueueSize() != 0 && this.operatingSystem.getCpuList() != null) {
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
        this.operatingSystem.setScheduler(new pFirstComeFirstServed());

        // Deshabilitar botones iniciales
        this.createButton.setEnabled(false);
        this.startButton.setEnabled(false);

        // Lanzar simulacion inicial
        this.startSimulationDialog.dispose();

        // INICIAR SIMULACION
        this.operatingSystem.startSystem();
    }//GEN-LAST:event_fcfsButtonActionPerformed

    private void createCpusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCpusButtonActionPerformed
        // TODO add your handling code here:
        if (this.operatingSystem.getCpuList().getSize() < 3) {
            addCPUToSimulation();  // Añadir CPU si hay menos de 3 CPUs
        } else {
            JOptionPane.showMessageDialog(this, "Ya no se pueden crear más CPUs.", "Límite de CPUs", JOptionPane.WARNING_MESSAGE);
        }


    }//GEN-LAST:event_createCpusButtonActionPerformed

    private void deleteCpusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCpusButtonActionPerformed
        // TODO add your handling code here:
        if (this.operatingSystem.getCpuList().getSize() > 1) {
            this.removeCPUFromSimulation();
        } else {
            JOptionPane.showMessageDialog(this, "Ya no se pueden eliminar más CPUs.", "Límite de CPUs", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_deleteCpusButtonActionPerformed

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProcessButtonActionPerformed
        // TODO add your handling code here:
        if (this.operatingSystem.getCpuList() == null || this.operatingSystem.getCpuList().getSize() == 0) {
            JOptionPane.showMessageDialog(this, "Error, todavía no se han creado CPUs en el sistema!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

    }//GEN-LAST:event_createProcessButtonActionPerformed

    private void cycleDurationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cycleDurationSliderStateChanged
        // TODO add your handling code here:
        int newSpeed = this.cycleDurationSlider.getValue();
        System.out.println(newSpeed);
        operatingSystem.getSystemClock().setCycleDuration(newSpeed);
    }//GEN-LAST:event_cycleDurationSliderStateChanged

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
    private javax.swing.JButton createProcessButton;
    private javax.swing.JTextField cycleDurationField;
    private javax.swing.JSlider cycleDurationSlider;
    private javax.swing.JButton deleteCpusButton;
    private javax.swing.JButton fcfsButton;
    private javax.swing.JMenuItem fcfsMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel listFinishedPanel;
    private javax.swing.JButton loadButton;
    private javax.swing.JPanel mainSimulationPanel;
    private javax.swing.JTextField numCPUsField;
    private javax.swing.JTextField numProcessesField;
    private javax.swing.JPanel processDetailsPanel;
    private javax.swing.JTable processJTable;
    private javax.swing.JScrollPane processTable;
    private javax.swing.JPanel processTablePanel;
    private javax.swing.JPanel queueBlockedPanel;
    private javax.swing.JPanel queueReadyPanel;
    private javax.swing.JScrollPane scrollCpusPane;
    private javax.swing.JMenu simulationOptions;
    private javax.swing.JPanel simulationPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JDialog startSimulationDialog;
    private javax.swing.JButton stopButton;
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
}
