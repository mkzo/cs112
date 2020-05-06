package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

    public static String delims = " \t*+-/()[]";
            
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        expr = expr.replaceAll("\\s",""); 
        String arrayPatternString = "[a-zA-Z]+(?=\\[)";
        String varPatternString = "[a-zA-Z]+(?![\\[\\w])";
        Matcher a = Pattern.compile(arrayPatternString)
                .matcher(expr);
        Matcher b = Pattern.compile(varPatternString)
                .matcher(expr);
        while (a.find()) {
            arrays.add(new Array(a.group()));
        }
        while (b.find()) {
            vars.add(new Variable(b.group()));
        }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
                continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
                arr = arrays.get(arri);
                arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float
    evaluate(String expr, ArrayList < Variable > vars, ArrayList < Array > arrays) {
    	ArrayList <Float> operand = new ArrayList <Float>() ;
        ArrayList <Character> operator = new ArrayList <Character>();
        for (int i = 0; i < expr.length(); i++) //iterate
        {
            if (Character.isLetter(expr.charAt(i))) {
                int beginindex = i;
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) //check for arrays
                {
                    i++;
                }
                if (i != expr.length() && expr.charAt(i) == '[') {
             
                    int endindex = i;
                    int k = 1;
                    while (k > 0) {
                        endindex++;
                        char c = expr.charAt(endindex);
                        if (c == '[') {
                            k++;
                        } else if (c == ']') {
                            k--;
                        }

                    }
                    Float arrayIndex = new Float(evaluate(expr.substring(i + 1, endindex), vars, arrays));

                    for (int j = 0; j < arrays.size(); j++) {
                        if (expr.substring(beginindex, i).equals(arrays.get(j).name)) {
                            int ind = arrayIndex.intValue();
                            Float IntegerObject = new Float(arrays.get(j).values[ind]);
                            System.out.println("AAA"+IntegerObject);
                            operand.add(IntegerObject);
                            break;
                        }
                    }
                    i = endindex;
                } 
               

                else //means it has to be a var
                {
                    for (int j = 0; j < vars.size(); j++) {
                        if (expr.substring(beginindex, i).equals(vars.get(j).name)) {
                            Float IntegerObject = new Float(vars.get(j).value);
                            operand.add(IntegerObject);
                            break;
                        }
                    }
                    i--;
                }

            }
            if (i < expr.length() && expr.charAt(i) == '(') {
                int endindex = i;
                int k = 1;
                while (k > 0) {
                    endindex++;
                    char c = expr.charAt(endindex);
                    if (c == '(') {
                        k++;
                    }
                    else if (c == ')') {
                        k--;
                    }

                }
                Float arrayIndex = new Float(evaluate(expr.substring(i + 1, endindex), vars, arrays));
                i = endindex;
                operand.add(arrayIndex);
            }
            if (expr.charAt(i) == '+') {
                operator.add('+');
            }
            if (expr.charAt(i) == '-') {
                operator.add('-');
            }
            if (expr.charAt(i) == '*') {
                operator.add('*');
            }
            if (expr.charAt(i) == '/') {
                operator.add('/');
            }
            if (Character.isDigit(expr.charAt(i))) {
                int beginindex = i;
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    i++;
                }
                Float IntegerObject = new Float(Float.parseFloat(expr.substring(beginindex, i)));
                operand.add(IntegerObject);
                i--;
            }
        }
        System.out.println("r");
        for (int i= 0; i < operator.size(); i++) {
            System.out.println(operator.get(i));
        }
        System.out.println("operand");
        for (int i= 0; i < operand.size(); i++) {
            System.out.println(operand.get(i));
        }
        System.out.println("r");
        return trueEvaluate(operator, operand);
        }
private static float
trueEvaluate(ArrayList<Character>operator, ArrayList<Float>operand) {
    if (operand.size() == 1) {
        return operand.get(0);
    }
    else {
        if (operator.size() == 1) {
            operator.add('+');
        }
        if (operator.get(0) == '+' && (operator.get(1) == '-' || operator.get(1) == '+')) {
            operand.set(1, operand.get(0) + operand.get(1));
            operand.remove(0);
            operator.remove(0);
            return trueEvaluate(operator,operand);
        }
        if (operator.get(0) == '-' && (operator.get(1) == '-' || operator.get(1) == '+')) {
            operand.set(1, operand.get(0) - operand.get(1));
            operand.remove(0);
            operator.remove(0);
            return trueEvaluate(operator,operand);
        }
        if (operator.get(0) == '*') {
            operand.set(1, operand.get(0) * operand.get(1));
            operand.remove(0);
            operator.remove(0);
            return trueEvaluate(operator,operand);
        }
        if (operator.get(0) == '/') {
            operand.set(1, operand.get(0) / operand.get(1));
            operand.remove(0);
            operator.remove(0);
            return trueEvaluate(operator,operand);
        }
        if (operator.get(0) == '+' && (operator.get(1) == '*' || operator.get(1) == '/')) {
            Float a = operand.get(0);
            operand.remove(0);
            operator.remove(0);
            return a + trueEvaluate(operator,operand);
        }
        if (operator.get(0) == '-' && (operator.get(1) == '*' || operator.get(1) == '/')) {
            Float a = operand.get(0);
            operand.remove(0);
            operator.remove(0);
            return a - trueEvaluate(operator,operand);
        }
    }
    return 0;
}   
}