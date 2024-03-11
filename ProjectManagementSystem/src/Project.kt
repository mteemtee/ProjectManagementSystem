import java.io.*

// Class representing a project with tasks and dependencies
class Project(val projectName: String) : Serializable {
    // List to store tasks in the project
    val tasks: MutableList<Task> = mutableListOf()

    // Constructor: Load project details from file when a new project is created
    init {
        loadProjectDetails()
    }
    override fun toString(): String {
        return projectName
    }

    // Function to add a task to the project
    fun addTask(task: Task) {
        tasks.add(task)

        // Update the adjacency matrix and save the project details
        updateAdjacencyMatrixAndSave()
    }

    // Function to update the adjacency matrix and save project details
    private fun updateAdjacencyMatrixAndSave() {
        val taskList = tasks
        val matrix = Array(taskList.size) { IntArray(taskList.size) }

        for ((index, currentTask) in taskList.withIndex()) {
            for (dependency in currentTask.dependencies) {
                val dependencyIndex = taskList.indexOf(dependency)
                matrix[dependencyIndex][index] = 1
            }
        }

        // Save the adjacency matrix to the current project
        for ((index, currentTask) in taskList.withIndex()) {
            for (dependencyIndex in matrix[index].indices) {
                if (matrix[index][dependencyIndex] == 1) {
                    // Add the dependency without clearing existing ones
                    currentTask.dependencies.add(taskList[dependencyIndex])
                }
            }
        }

        saveProjectDetails()
    }


    // Function to update the adjacency matrix
    fun updateAdjacencyMatrix(): Array<IntArray> {
        val taskList = tasks
        val matrix = Array(taskList.size) { IntArray(taskList.size) }

        for ((index, task) in taskList.withIndex()) {
            // Check if the task has dependencies
            if (task.dependencies.isNotEmpty()) {
                for (dependency in task.dependencies) {
                    // Find the index of the dependency in the taskList
                    val dependencyIndex = taskList.indexOf(dependency)

                    // Ensure the dependency is found in the taskList
                    if (dependencyIndex != -1) {
                        // Update the matrix
                        matrix[dependencyIndex][index] = 1
                    } else {
                        // Handle the case where the dependency is not found
                        println("Dependency not found in taskList: ${dependency.taskName}")
                    }
                }
            }
        }

        return matrix
    }

    // Function to get task names as an array
    fun getTaskNames(): Array<String> {
        return tasks.map { it.taskName }.toTypedArray()
    }

    // Function to get a task by its name
    fun getTaskByName(name: String): Task? {
        return tasks.find { it.taskName == name }
    }

    fun resetProject() {
        tasks.clear()
        // You may need to reset other project-specific details if necessary
    }

    // Function to save project details to a file
    public fun saveProjectDetails() {
        val file = File("$projectName.txt")
        file.bufferedWriter().use { writer ->
            // Serialize the project object and write it to the file
            writer.write(SerializationUtil.serializeObject(this))
        }
    }

    // Function to load project details from a file
    public fun loadProjectDetails() {
        val file = File("$projectName.txt")
        if (file.exists()) {
            file.bufferedReader().use { reader ->
                // Deserialize the project object from the file
                val serializedData = reader.readText()
                val loadedProject = SerializationUtil.deserializeObject(serializedData, Project::class.java)
                // Update the current project with the loaded details
                tasks.clear()
                tasks.addAll(loadedProject.tasks)
            }
        }
    }
}

// Object to provide utility functions for object serialization
object SerializationUtil {
    // Function to serialize an object to a string
    fun serializeObject(obj: Serializable): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use { it.writeObject(obj) }
        return byteArrayOutputStream.toString("ISO-8859-1")
    }

    // Function to deserialize an object from a string
    fun <T : Serializable> deserializeObject(serializedData: String, clazz: Class<T>): T {
        val byteArrayInputStream = ByteArrayInputStream(serializedData.toByteArray(Charsets.ISO_8859_1))
        return ObjectInputStream(byteArrayInputStream).use { it.readObject() as T }
    }
}

// Object to convert a project's graph to an adjacency matrix string
object GraphConverter {
    // Function to convert a project's graph to an adjacency matrix string
    fun convertToAdjacencyMatrix(project: Project): String {
        val taskNames = project.getTaskNames()
        val matrix = project.updateAdjacencyMatrix()

        val matrixStringBuilder = StringBuilder("   ")
        for (taskName in taskNames) {
            matrixStringBuilder.append("$taskName ")
        }
        matrixStringBuilder.append("\n")

        for ((index, row) in matrix.withIndex()) {
            matrixStringBuilder.append("${taskNames[index]} ")
            for (value in row) {
                matrixStringBuilder.append("$value ")
            }
            matrixStringBuilder.append("\n")
        }

        return matrixStringBuilder.toString()
    }
}

// Main function for testing
fun main() {
    // Create a project
    val project = Project("TestProject")

    // Create tasks
    val taskA = Task("a")
    val taskB = Task("b")
    val taskC = Task("c")

    // Add tasks to the project
    project.addTask(taskA)
    project.addTask(taskB)
    project.addTask(taskC)

    // Adding dependencies for each task to every other task
    taskA.addDependency(taskB)
    taskA.addDependency(taskC)
    taskA.addDependency(taskA)

    taskB.addDependency(taskA)
    taskB.addDependency(taskC)
    taskB.addDependency(taskB)

    taskC.addDependency(taskA)
    taskC.addDependency(taskB)
    taskC.addDependency(taskC)

    // Convert to and display the adjacency matrix
    val adjacencyMatrix = GraphConverter.convertToAdjacencyMatrix(project)
    println(adjacencyMatrix)
}
