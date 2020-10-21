package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) { //first call of solve()
        ImList<Clause> clauses = formula.getClauses();
        Environment env = new Environment();
        return solve(clauses, env); //second call of solve(); recursion
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // If there are no clauses, the formula is trivially satisfiable.
        if (clauses.isEmpty()){
            return env;
        }
        /*
        If there is an empty clause, the clause list is unsatisfiable -- fail and backtrack.
        (use empty clause to denote a clause evaluated to FALSE based on the variable binding in the environment)
         */
        for (Clause c : clauses){
            if (c.isEmpty()){
                return null;
            }
        }
        //Otherwise, find the smallest clause (by number of literals)
        Clause smallestClause=clauses.first(); //initialise as first Clause
        for (Clause c: clauses){
            if (c.size()<smallestClause.size()){
                smallestClause=c;
            }
        }

        /*
        substitute for the variable in all the other clauses (using the
        suggested substitute() method), and recursively call solve().
        */
        Literal l = smallestClause.chooseLiteral();                 //pick an arbitrary literal from smallestClause
        Variable v = l.getVariable();
        Environment newEnv = new Environment();

        if (smallestClause.isUnit()){                               //If the clause has only one literal,
            if (l instanceof PosLiteral){                           //(positive literal)
                newEnv = env.putTrue(v);                            //bind its variable in the environment so that the clause is satisfied
            } else {
                newEnv = env.putFalse(v);
            }
            ImList<Clause> subClauses =  substitute(clauses, l);    //substitute for the variable in all the other clauses
            return (solve(subClauses, newEnv));                     //solve() recursively

        }else{                                                      // Otherwise (if clause has >1 literal),

            Environment tryTrue = env.putTrue(v);                   //try setting the literal to TRUE
            ImList<Clause> clausesTrue = substitute(clauses, l);    //substitute for it in all the clauses
            Environment envTrue=solve(clausesTrue,tryTrue);         //solve() recursively

            if (envTrue == null) {                                  // If that fails,
                Environment tryFalse = env.putFalse(v);             // then try setting the literal to FALSE
                ImList<Clause> clausesFalse = substitute(clauses, l.getNegation()); //substitute
                Environment envFalse = solve(clausesFalse, tryFalse); //solve() recursively
                return envFalse;
            } else{
                return envTrue;
            }
        }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,Literal l) {
        ImList<Clause> newClauses=new EmptyImList<>();
        for (Clause c : clauses){
            Clause newC=c.reduce(l);    //parse through all clauses, change literal to true using reduce()
            if (newC!=null){            //if new clause is not true, add to ImList
                newClauses=newClauses.add(newC);
            }
        }
        return newClauses;
    }
}