// Class responsible for managing projects and providing adjacency matrix information
class ProjectManager {
    // List to store projects managed by the project manager
    private val projects: MutableList<Project> = mutableListOf()

    // Function to add a project to the manager
    fun addProject(project: Project) {
        projects.add(project)
    }

    // Function to get the adjacency matrix as a string for a given project
    fun getAdjacencyMatrix(project: Project): String {
        // Delegates the conversion to the GraphConverter class
        return GraphConverter.convertToAdjacencyMatrix(project)
    }

    fun removeProject(project: Project?) {
        projects.remove(project)
    }
}
