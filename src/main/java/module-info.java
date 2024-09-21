/**
 * A high-performance Physics Engine built using an Entity Component System (ECS) architecture, 
 * designed with a hybrid approach that blends object-oriented programming (OOP) principles 
 * with Data-Oriented Design (DOD) to deliver both intuitive usage and exceptional performance.
 * The engine is tailored for applications that demand efficient, real-time simulation of physics, 
 * such as games or simulations with large-scale environments.
 * 
 * <p>At its core, this ECS framework aims to provide developers with a familiar API structure 
 * while maximising data locality and reducing memory overhead through flat, contiguous 
 * primitive arrays. By avoiding the typical object-oriented overhead (e.g., generics, 
 * object wrappers, and polymorphism at runtime), the ECS achieves a much lower memory footprint 
 * and ensures data is more cache-friendly, thus improving performance on modern CPUs where 
 * cache misses are a significant bottleneck.</p>
 * 
 * <p>The foundational components of the ECS are the {@code IntList} and {@code FloatList} classes, 
 * which implement dynamic flat arrays of primitive types (integers and floats). These classes are 
 * designed to closely mimic Java’s List interface but avoid the use of generics and object 
 * wrapping to eliminate the inherent memory overhead that comes with generic List implementations. 
 * All primitive data associated with components is stored in these lists, ensuring efficient memory 
 * access patterns and minimising cache misses. By using flat arrays, the engine guarantees that 
 * component data is stored contiguously in memory, which is a crucial aspect of modern game 
 * performance optimisation.</p>
 * 
 * <p>Although the ECS does not strictly adhere to a traditional Archetype-based memory layout, 
 * Archetypes still feature prominently in its design. Each archetype corresponds to a specific 
 * combination of components, represented as a bitmask. However, instead of storing entire entities 
 * and their components contiguously in memory (as in typical Archetype systems), this ECS stores 
 * each component type in its own flat array. This allows for contiguous memory storage for 
 * individual components, leading to more efficient memory access during system execution, 
 * particularly for data-heavy operations like physics calculations.</p>
 * 
 * <p>The ECS utilises a {@code SparseSet} architecture to map entities to components, achieving 
 * constant-time O(1) lookups for entity-component associations. The {@code SparseSet} itself 
 * maintains a static sparse array and a dynamic flat array (an instance of {@code IntList}), 
 * making the component lookup and assignment process highly efficient, even for large numbers 
 * of entities.</p>
 * 
 * <p>There are two primary categories of components in this ECS: {@code Vector} and {@code Scalar} 
 * components. Components that involve multi-dimensional data (such as positions or velocities) 
 * are stored using {@code VectorStorage}, while single-value components (such as health or mass) 
 * use {@code ScalarStorage}. This distinction allows the ECS to handle both scalar and vector 
 * data efficiently, using memory layouts tailored for each data type. Specialised data structures 
 * such as those for geometric shapes (e.g., {@code Circle}, {@code Rectangle}) implement 
 * interleaved flat arrays to store their complex data, ensuring contiguity while balancing the 
 * need for multi-attribute components.</p>
 * 
 * <p>Each component is identified and assigned a unique bit within a {@code Component} enum, 
 * which forms the basis for defining Archetypes. The {@code Archetype} enum groups components 
 * into unique bitsets that systems can use to query entities of interest efficiently. This use 
 * of bitmasks enables rapid querying, iteration, and manipulation of entities that match a given 
 * set of components, facilitating fast execution of systems that operate on specific subsets of 
 * entities.</p>
 * 
 * <p>The design choices behind this engine—dynamic flat arrays for data storage, sparse set for 
 * entity-component mapping, and hybrid use of OOP and DOD—were made to maximise both developer 
 * productivity and system performance. Developers familiar with OOP can quickly get up to speed 
 * with the API, while the engine under the hood performs optimally, making it suitable for 
 * large-scale, real-time physics simulations. Furthermore, the framework is highly extensible, 
 * allowing for future features such as more advanced physics models, spatial partitioning, 
 * and parallel processing to be integrated seamlessly.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Hybrid ECS architecture combining OOP and Data-Oriented Design for a developer-friendly yet performant framework</li>
 *   <li>Optimised for CPU cache locality and data contiguity through flat primitive arrays</li>
 *   <li>Efficient entity-to-component mapping using a SparseSet architecture for O(1) lookup times</li>
 *   <li>Support for both Vector and Scalar components with specialised memory storage for complex data types</li>
 *   <li>Flexible bitmask-based system for rapid querying of Archetypes and efficient entity iteration</li>
 *   <li>Designed for extensibility, with future features like advanced physics models and parallel processing in mind</li>
 * </ul>
 */
module com.slinky.physics {
    exports com.slinky.physics;
    
    // Javadoc exports
    exports com.slinky.physics.base;
    exports com.slinky.physics.components;
    exports com.slinky.physics.util;
}