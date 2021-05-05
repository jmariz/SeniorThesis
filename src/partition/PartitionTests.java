package partition;

import org.junit.Test;

/**
 * Joshua Mariz 05/05/2021
 * PartitionTests.java
 * PartitionTests allows us to run the following classes
 * - Partitioner.java
 * - SingleDistricter.java
 * - UnboundedSingleDistricter.java
 * - WeightedPartitioner.java
 * - WeightedSingleDistricter.java
 * - WeightedUnboundedSingleDistricter.java
 * NOTE: The weighting factor probability argument must be greater than or equal to 0 AND less than or equal to 100.
 */

public class PartitionTests {
    
    @Test
    public void Partition_t0() {
        // UNCOMMENT OUT THE FILE YOU WANT TO RUN; TRY DIFFERENT ARGUMENTS
           Partitioner.Partition(5, 2);                             // Partitioner.Partition(Squaretopia Size, Number of Partitions);
        // SingleDistricter.Partition(5, 2);                        // SingleDistricter.Partition(Squaretopia Size, Number of Single Districts);
        // UnboundedSingleDistricter.Partition(5, 2);               // UnboundedSingleDistricter.Partition(Squaretopia Size, Number of Single Districts);
        // WeightedPartitioner.Partition(5, 2, 50);                 // WeightedPartitioner.Partition(Squaretopia Size, Number of Partitions, Weighting Factor Probability);
        // WeightedSingleDistricter.Partition(5, 2, 50);            // WeightedSingleDistricter.Partition(Squaretopia Size, Number of Single Districts, Weighting Factor Probability);
        // WeightedUnboundedSingleDistricter.Partition(5, 2, 50);   // WeightedUnboundedSingleDistricter.Partition(Squaretopia Size, Number of Single Districts, Weighting Factor Probability);
    }
    
}
