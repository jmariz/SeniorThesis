package partition;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import java.time.LocalDate;
import java.util.*;

public class PartitionTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite + to test that your
    // constraint propagation is working...
    // If they are, 3 seconds should be more than enough
    // for any test
////    @Rule
////    public Timeout globalTimeout = Timeout.seconds(9);
    
//    /**
//     * Tests whether a given solution to a CSP satisfies all constraints or not
//     * @param soln Full instantiation of variables to assigned values, indexed by variable
//     * @param constraints The set of constraints the solution must satisfy
//     */
//    public static void testSolution (List<LocalDate> soln, Set<DateConstraint> constraints) {
//        for (DateConstraint d : constraints) {
//            LocalDate leftDate = soln.get(d.L_VAL),
//                      rightDate = (d.arity() == 1) 
//                          ? ((UnaryDateConstraint) d).R_VAL 
//                          : soln.get(((BinaryDateConstraint) d).R_VAL);
//            
//            boolean sat = false;
//            switch (d.OP) {
//            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
//            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
//            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
//            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
//            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
//            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
//            }
//            if (!sat) {
//                fail("[X] Constraint Failed: " + d);
//            }
//        }
//    }
    
    
    // =================================================
    // Unit Tests
    // =================================================
    
    @Test
    public void Partition_t0() {
//        Set<DateConstraint> constraints = new HashSet<>(
//            Arrays.asList(
//                new UnaryDateConstraint(0, "==", LocalDate.of(2020, 1, 3))
//            )
//        );
        
        // Date range of 2020-1-1 to 2020-1-5 in which the only meeting date
        // for 1 meeting can be on 2020-1-3
//        List<LocalDate> solution = CSP.solve(
//            1,                          // Number of meetings to schedule
//            LocalDate.of(2020, 1, 1),   // Domain start date
//            LocalDate.of(2020, 1, 5),   // Domain end date
//            constraints                 // Constraints all meetings must satisfy
//        );
        
        
        // Partitioner.Partition(5, 2);
        // SingleDistricter.Partition(5, 10000);
        UnboundedSingleDistricter.Partition(5, 10000);
        
        
        
//        SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(5, 5);
//        Squaretopia.show();
//        System.out.println(""); // delete
//        Squaretopia.data[1][2].districtNumber = 1;
//        Squaretopia.data[1][2].checked = true;
////        Squaretopia.data[1][3].districtNumber = 1;
////        Squaretopia.data[1][3].checked = true;
//        Squaretopia.data[3][1].districtNumber = 1;
//        Squaretopia.data[3][1].checked = true;
////        Squaretopia.data[2][1].districtNumber = 1;
////        Squaretopia.data[2][1].checked = true;
//        Squaretopia.data[2][2].districtNumber = 1;
//        Squaretopia.data[2][2].checked = true;
////        Squaretopia.data[2][3].districtNumber = 1;
////        Squaretopia.data[2][3].checked = true;
//        Squaretopia.data[1][1].districtNumber = 1;
//        Squaretopia.data[1][1].checked = true;
//        Squaretopia.data[3][2].districtNumber = 1;
//        Squaretopia.data[3][2].checked = true;
//        Squaretopia.show();
//        System.out.println(Squaretopia.validMap(Squaretopia)); // delete
        // Example Solution:
        // [2020-01-03]
        // testSolution(solution, constraints);
    }
    
}
