import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class Task(val taskName: String) : Serializable {
    // Initialize dependencies as an empty list
    val dependencies: MutableList<Task> = mutableListOf()

    fun addDependency(task: Task) {
        dependencies.add(task)
        task.dependencies.add(this)
    }

    // Custom serialization method
    private fun writeObject(out: ObjectOutputStream) {
        out.defaultWriteObject()
        // Write the number of dependencies
        out.writeInt(dependencies.size)
        // Write each dependency by taskName
        dependencies.forEach { out.writeObject(it.taskName) }
    }

    // Custom deserialization method
    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
        // Initialize dependencies as an empty list
        val dependencies: MutableList<Task> = mutableListOf()
        // Read the number of dependencies
        val numDependencies = `in`.readInt()
        // Read each dependency by taskName and add it to the list
        repeat(numDependencies) {
            val dependencyName = `in`.readObject() as String
            dependencies.add(Task(dependencyName))
        }
        // Assign the initialized dependencies list
        this.dependencies.addAll(dependencies)
    }
}
