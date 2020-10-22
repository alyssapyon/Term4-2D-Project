package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.*;

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


    // TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    public static void main(String[] args) {
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (args.length == 0) {
            System.out.println("ERROR: NO .CNF FILE PASSED IN");
        } else {
            // PART 1: CNF FILE TO FORMULA

            try {
                // open file with BufferedReader
                // BufferedReader reader = new BufferedReader(new FileReader("sat1.cnf"));
                // BufferedReader reader = new BufferedReader(new FileReader("test_2020.cnf"));
                Path path = Paths.get(args[0]);
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));

                // declare variables
                // use ArrayList for easy insertion of each literal/clause
                String newLine = null;
                ArrayList<Clause> clauses = new ArrayList<Clause>();
                ArrayList<Literal> literals = new ArrayList<Literal>();

                // ensure the line is not empty
                while ((newLine = reader.readLine()) != null) {
                    // ignore the line if its a comment/problem line
                    if (newLine.length() <= 0 || newLine.charAt(0) == 'p' || newLine.charAt(0) == 'c') {
                        continue;
                        // only continue if its a clause line
                    } else {
                        // remove newline and empty spaces
                        // turn the line of String into an array
                        newLine = newLine.trim();
                        newLine = newLine.replace("\n", "");
                        String[] newSplitLine = newLine.split("\\s+");

                        // for each element in the array, create a matching literal to add into the clause
                        for (String i : newSplitLine) {
                            int numi = Integer.parseInt(i);
                            // numi == 0 indicates the end of a clause
                            if (numi == 0) {
                                // add everything into a clause
                                Literal[] clauseLiterals = literals.toArray(new Literal[literals.size()]);
                                Clause newClause = makeCl(clauseLiterals);
                                clauses.add(newClause);
                                literals.clear();

                            } else if (numi > 0) {
                                // numi is positive literal
                                Literal lit = PosLiteral.make(String.valueOf(numi));
                                literals.add(lit);
                            } else {
                                // numi is negative literal
                                int positiveValue = numi * -1;
                                Literal posLit = PosLiteral.make(String.valueOf(positiveValue));
                                Literal lit = posLit.getNegation();
                                literals.add(lit);
                            }


                        }
                    }
                } // finish the while section

                ////////////////////////////////////////////////////////////////////////////////////////

                // PART 2: PUT FORMULA INTO SOLVER (copy paste code from handout)

                // combine clauses into a single formula
                Clause[] formulaClauses = clauses.toArray(new Clause[clauses.size()]);
                Formula f2 = makeFm(formulaClauses);

                System.out.println("SAT solver starts!!!");
                long started = System.nanoTime();
                Environment e = SATSolver.solve(f2);
                long time = System.nanoTime();
                long timeTaken = time - started;
                System.out.println("Time:" + timeTaken / 1000000.0 + "ms");

                ////////////////////////////////////////////////////////////////////////////////////////

                // PART 3: WRITE OUTPUT TO NEW FILE
                // PrintWriter writer = new PrintWriter("outputfile.txt");
                PrintWriter writer = new PrintWriter(args[1]);
                writer.println("Time:" + timeTaken / 1000000.0 + "ms");
                PrintWriter writer = new PrintWriter(args[1]);
                if (e == null) {
                    writer.println("not satisfiable");
                } else {
                    writer.println("satisfiable");
                    String envString = e.toString();
                    envString = envString.substring(13, envString.length() - 1);
                    envString = envString.replaceAll(", ", "\n");
                    writer.println(envString);
                }
                writer.close();
                System.out.println("EVERYTHING HAS BEEN DONE SUCCESSFULLY");

            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }


    public void testSATSolver1() {
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a, b)));
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }


    public void testSATSolver2() {
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }


}