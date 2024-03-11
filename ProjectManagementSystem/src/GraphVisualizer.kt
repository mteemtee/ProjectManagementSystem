import com.mxgraph.layout.mxCompactTreeLayout
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import java.awt.Dimension
import javax.swing.JFrame
import kotlin.math.pow

// Class to visualize the project tasks and dependencies using a graph
class GraphVisualizer(private val project: Project) {

    // Function to visualize the graph
    fun visualizeGraph() {
        // Create a new graph instance
        val graph = mxGraph()
        // Get the default parent of the graph
        val parent = graph.defaultParent

        // Begin updating the graph model
        graph.model.beginUpdate()
        try {
            // Map to store vertices of tasks
            val vertexMap = mutableMapOf<Task, Any>()
            // Initial coordinates for placing vertices
            var x = 20.0
            var y = 20.0

            // Add vertices for each task
            for (task in project.tasks) {
                // Insert a vertex for the task
                val vertex = graph.insertVertex(parent, null, task.taskName, x, y, 80.0, 30.0)
                // Store the vertex in the map
                vertexMap[task] = vertex
                // Update coordinates for the next vertex
                x += 100.0
                y += 50.0
            }

            // Add edges based on task dependencies
            for (task in project.tasks) {
                // Get the source vertex
                val sourceVertex = vertexMap[task]
                for (dependency in task.dependencies) {
                    // Get the target vertex for each dependency
                    val targetVertex = vertexMap[dependency]
                    // Insert an edge between the source and target vertices
                    graph.insertEdge(parent, null, "", sourceVertex, targetVertex)
                }
            }
        } finally {
            // End updating the graph model
            graph.model.endUpdate()
        }

        // Create a graph component to display the graph
        val graphComponent = mxGraphComponent(graph)
        // Create a compact tree layout for better visualization
        val layout = mxCompactTreeLayout(graph, false)
        layout.execute(parent)

        // Create a JFrame to display the graph component
        val frame = JFrame("Graph Visualization")
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.contentPane.add(graphComponent)
        frame.size = Dimension(600, 600)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
