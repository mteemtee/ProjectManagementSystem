import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JFrame {
    private final DefaultListModel<Project> projectListModel;
    private final JList<Project> projectList;
    private final JTextArea adjacencyMatrixTextArea;
    private final ProjectManager projectManager;

    public Main() {
        super("Project Management System");

        projectManager = new ProjectManager();
        projectListModel = new DefaultListModel<>();
        projectList = new JList<>(projectListModel);
        adjacencyMatrixTextArea = new JTextArea();

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Initialize GUI components
    private void initComponents() {
        // Button to add a new project
        JButton addProjectButton = new JButton("Add Project");
        addProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String projectName = JOptionPane.showInputDialog("Enter project name:");
                if (projectName != null && !projectName.trim().isEmpty()) {
                    Project project = new Project(projectName);
                    projectManager.addProject(project);
                    projectListModel.addElement(project);
                }
            }
        });

        // Button to add tasks to the selected project
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    int totalTasks = Integer.parseInt(JOptionPane.showInputDialog("Enter the total number of tasks:"));
                    String[] taskNames = new String[totalTasks];
                    for (int i = 0; i < totalTasks; i++) {
                        taskNames[i] = JOptionPane.showInputDialog("Enter task name:");
                        Task task = new Task(taskNames[i]);
                        selectedProject.addTask(task);
                    }

                    for (int i = 0; i < totalTasks; i++) {
                        Task task = selectedProject.getTaskByName(taskNames[i]);
                        assert task != null;
                        addTaskDependencies(selectedProject, task);
                    }

                    updateAdjacencyMatrixTextArea();
                }
            }
        });

        // Button to visualize the graph of the selected project
        JButton visualizeGraphButton = new JButton("Visualize Graph");
        visualizeGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    new GraphVisualizer(selectedProject).visualizeGraph();
                }
            }
        });

        // Button to save project details
        JButton saveButton = new JButton("Save Project Details");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    selectedProject.saveProjectDetails();
                    JOptionPane.showMessageDialog(Main.this, "Project details saved successfully.");
                }
            }
        });

        // Button to load project details
        JButton loadButton = new JButton("Load Project Details");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    selectedProject.loadProjectDetails();
                    updateAdjacencyMatrixTextArea();
                    JOptionPane.showMessageDialog(Main.this, "Project details loaded successfully.");
                }
            }
        });

        // Button to delete the selected project
        JButton deleteProjectButton = new JButton("Delete Project");
        deleteProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    projectListModel.removeElement(selectedProject);
                    projectManager.removeProject(selectedProject);
                    adjacencyMatrixTextArea.setText(""); // Clear adjacency matrix when a project is deleted
                }
            }
        });

        // Button to edit the selected project (reset and start again)
        JButton editProjectButton = new JButton("Edit Project");
        editProjectButton .addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = projectList.getSelectedValue();
                if (selectedProject != null) {
                    // Reset the project details
                    selectedProject.resetProject();
                    adjacencyMatrixTextArea.setText(""); // Clear adjacency matrix after resetting

                    // Prompt the user to add tasks and dependencies again
                    int totalTasks = Integer.parseInt(JOptionPane.showInputDialog("Enter the total number of tasks:"));
                    String[] taskNames = new String[totalTasks];
                    for (int i = 0; i < totalTasks; i++) {
                        taskNames[i] = JOptionPane.showInputDialog("Enter task name:");
                        Task task = new Task(taskNames[i]);
                        selectedProject.addTask(task);
                    }

                    for (int i = 0; i < totalTasks; i++) {
                        Task task = selectedProject.getTaskByName(taskNames[i]);
                        assert task != null;
                        addTaskDependencies(selectedProject, task);
                    }

                    // Update the adjacency matrix text area
                    updateAdjacencyMatrixTextArea();

                    JOptionPane.showMessageDialog(Main.this, "Project reset successfully.");
                }
            }
        });
        // Panel to hold the project list
        JPanel panel = new JPanel(new BorderLayout());

// Label for the project list
        JLabel projectLabel = new JLabel("Projects list");
        projectLabel.setFont(new Font("Helvatica", Font.BOLD, 16));
        projectLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

// Panel to hold the project list and label
        JPanel projectListPanel = new JPanel(new BorderLayout());
        projectListPanel.add(projectLabel, BorderLayout.NORTH);

// Create the JScrollPane without a titled border
        JScrollPane projectListScrollPane = new JScrollPane(projectList);

        projectListPanel.add(projectListScrollPane, BorderLayout.CENTER);

        panel.add(projectListPanel, BorderLayout.WEST);

// Panel to hold the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addProjectButton);
        buttonPanel.add(addTaskButton);
        buttonPanel.add(visualizeGraphButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(deleteProjectButton);
        buttonPanel.add(editProjectButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

// Text area to display the adjacency matrix
        JTextArea adjacencyMatrixTextArea = new JTextArea();
        JScrollPane adjacencyMatrixScrollPane = new JScrollPane(adjacencyMatrixTextArea);

// Label for the adjacency matrix inside the JScrollPane
        JLabel adjacencyMatrixLabel = new JLabel("Project Details");
        adjacencyMatrixLabel.setFont(new Font("Helvatica", Font.BOLD, 16));
        adjacencyMatrixLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
// Add the label and text area to the JScrollPane
        JPanel adjacencyMatrixPanel = new JPanel(new BorderLayout());
        adjacencyMatrixPanel.add(adjacencyMatrixLabel, BorderLayout.NORTH);
        adjacencyMatrixPanel.add(adjacencyMatrixScrollPane, BorderLayout.CENTER);

// Add the main panel to the frame
        panel.add(adjacencyMatrixPanel, BorderLayout.CENTER);
        add(panel);


    }

    // Add dependencies for a given task
    private void addTaskDependencies(Project selectedProject, Task task) {
        java.util.List<String> taskNames = new ArrayList<>(Arrays.asList(selectedProject.getTaskNames()));

        do {
            String selectedTask = (String) JOptionPane.showInputDialog(
                    this,
                    "Select dependencies for task '" + task.getTaskName() + "':",
                    "Add Dependencies",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    taskNames.toArray(),
                    null
            );

            if (selectedTask != null) {
                Task dependency = selectedProject.getTaskByName(selectedTask);
                if (dependency != null) {
                    task.addDependency(dependency);
                    dependency.addDependency(task);
                }
            } else {
                break;  // User canceled the selection
            }
        } while (true);
    }

    // Update the adjacency matrix text area
    private void updateAdjacencyMatrixTextArea() {
        Project selectedProject = projectList.getSelectedValue();
        if (selectedProject != null) {
            int[][] adjacencyMatrix = selectedProject.updateAdjacencyMatrix();
            StringBuilder matrixString = new StringBuilder("   ");

            for (String taskName : selectedProject.getTaskNames()) {
                matrixString.append(taskName).append(" ");
            }
            matrixString.append("\n");

            for (int i = 0; i < adjacencyMatrix.length; i++) {
                matrixString.append(selectedProject.getTaskNames()[i]).append(" ");
                for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                    matrixString.append(adjacencyMatrix[i][j]).append(" ");
                }
                matrixString.append("\n");
            }

            adjacencyMatrixTextArea.setText(matrixString.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
