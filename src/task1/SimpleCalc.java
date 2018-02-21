package task1;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;
import static jdk.nashorn.internal.objects.Global.Infinity;

public class SimpleCalc extends Exception {
/*
    Метод читает строки из документа input_2. txt в цикле,
    каждую строку сразу обрабатывает и записывает в документ output_2.txt,
*/
    public  void readWrite() throws IOException {
        FileReader fr;

        try {
            fr = new FileReader("src/task1/input_1.txt");
        } catch (FileNotFoundException e) {
            throw  new FileNotFoundException("File not found!");
        }

        BufferedReader br = new BufferedReader(fr);
        String input, output;
        double result;

        FileWriter fw ;

        try {
            fw = new FileWriter("src/task1/output_1.txt");
        } catch (IOException e) {
            throw  new IOException("Error writing to file!");
        }

        String formatDouble;

        while ( (input = br.readLine()) != null ){
            try {
                output = input;

                input = toRPN(input);
                result = calculate(input);

                Locale locale = new Locale("en", "UK");
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);

                if (result%1 == 0) {
                    formatDouble = "##0";
                } else {
                    formatDouble = "##0.00000";
                }
                DecimalFormat decimalFormat = new DecimalFormat(formatDouble, decimalFormatSymbols);
                String format = decimalFormat.format(result);

                if (result == Infinity) {
                    output = "Division by zero";
                    fw.write(output +"\n");
                } else {
                    fw.write(output + "=" + format + "\n");
                }
                fw.flush();
            } catch (Exception e) {
                fw.write(e.getMessage() + "\n");
            }
        }
        fw.close();
        br.close();
        fr.close();
    }

/*
    Преобразовываем строку в обратную польскую нотацию
*/
    private static String toRPN(String input) throws Exception {
        Stack<Character> stack = new Stack();
        String output = "";
        char cIn;
        boolean isSignOperation = false;
        boolean isLineBeginning = true;

        for (int i = 0; i < input.length(); i++) {
            cIn = input.charAt(i);

            if (isOperation(cIn)) {
                //проверка на унарный минус и унарный плюс
                if (((isSignOperation && cIn == '-' && !stack.empty()) || (isLineBeginning)) ||
                        (isSignOperation && cIn == '+' && !stack.empty()) || (isLineBeginning)) {
                    output += " " + cIn;
                    continue;
                }
                isSignOperation = true;

                output += " ";
                stack.push(cIn);
            } else {
                if(Character.isDigit(cIn)){
                    isSignOperation = false;
                    isLineBeginning = false;
                }
                output += cIn;
            }
        }

        while (!stack.empty()) {
            output += " " + stack.pop();
        }
        return  output;
    }

/*
    Проверка, является ли текущий символ оператором
*/
    private static boolean isOperation(char cIn) {
        switch (cIn) {
            case '-':
            case '+':
            case '*':
            case '/':
            case '^':
            case '%':
                return true;
        }
        return false;
    }

/*
    Метод для расчета выражения, записанного в обратной польской нотации
*/
    public static double calculate(String sIn) throws Exception {
        if (sIn == ""){
            throw new Exception("");
        }
        double dLeft = 0, dRight = 0;
        String strTmp;
        Stack<Double> stack = new Stack <>();
        StringTokenizer strToken = new StringTokenizer(sIn);
        while(strToken.hasMoreTokens()) {
            try {
                strTmp = strToken.nextToken().trim();
                if (1 == strTmp.length() && isOperation(strTmp.charAt(0))) {
                    if (stack.size() < 2) {
                        throw new Exception("Invalid amount of data in the stack " +
                                "to perform the operation " + strTmp);
                    }
                    dLeft = stack.pop();
                    dRight = stack.pop();
                    switch (strTmp.charAt(0)) {
                        case '+':
                            dLeft += dRight;
                            break;
                        case '-':
                            dLeft = dRight - dLeft;
                            break;
                        case '/':
                            dLeft = dRight / dLeft;
                            break;
                        case '*':
                            dLeft *= dRight;
                            break;
                        case '%':
                            dLeft *= dRight / 100;
                            break;
                        case '^':
                            dLeft = Math.pow(dRight,dLeft);
                            break;
                    }
                    stack.push(dLeft);
                } else {
                    dLeft = Double.parseDouble(strTmp);
                    stack.push(dLeft);
                }
            } catch (Exception e) {
                throw new Exception("Wrong expression!");
            }
        }
        if (stack.size() > 1) {
            throw new Exception("The number of operators" +
                    "does not match the number of operands!");
        }
        return stack.pop();
    }
}
