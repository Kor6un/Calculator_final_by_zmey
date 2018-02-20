package task1;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.StringTokenizer;
import static jdk.nashorn.internal.objects.Global.Infinity;

public class SimpleCalc extends Exception {
    public  void readWrite() throws IOException {
        FileReader fr;

        try {
            fr = new FileReader("src/task1/input_1.txt");
        } catch (FileNotFoundException e) {
            throw  new FileNotFoundException("Файл не найден");
        }

        BufferedReader br = new BufferedReader(fr);
        String strIn, strOut;
        double result;
        File file = new File("src/task1/output_1.txt");
        FileWriter fw;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            throw  new IOException("Ошибка записи в файл");
        }
        while ((strIn = br.readLine()) != null) {
            try {
                strOut = strIn;

                strIn = convertToRPN(strIn);
                result = calculate(strIn);

                String formatDouble;
                if (result%1 == 0) {
                    formatDouble = "##0";
                } else {
                    formatDouble = "##0.00000";
                }
                DecimalFormat decimalFormat = new DecimalFormat(formatDouble);
                String format = decimalFormat.format(result);

                if (result == Infinity || result == -Infinity) {
                    strOut = "Деление на 0!";
                    fw.write(strOut +"\n");
                } else {
                    fw.write(strOut + "=" + format + "\n");
                }

                fw.flush();
            } catch (Exception e) {
                fw.write(e.getMessage() + "\n");
            }
        }
        if (file.exists()){
            file.deleteOnExit();
            file = new File("src/task1/output_1.txt");
            fw = new FileWriter(file,true);
        }
        fw.close();
        br.close();
        fr.close();
    }
    /**
     * Преобразовать строку в обратную польскую нотацию
     * @param stringIn Входная строка
     * @return Выходная строка в обратной польской нотации
     */
    private static String convertToRPN(String stringIn) throws Exception {
        if(stringIn == "") {
            throw new Exception("");
        }
        String strStack = "";
        String strOut = "";
        char charIn, charTemp;

        for (int i = 0; i < stringIn.length(); i++) {
            charIn = stringIn.charAt(i);
            if (isOperation(charIn)) {
                if (strStack.length() > 0) {
                    while (strStack.length() > 0) {
                        //charTemp = strStack.substring(strStack.length()-1).charAt(0);
                        charTemp = strStack.charAt(strStack.length() - 1);
                        //TODO многоэтажные степени (3^2^2^2 = 3^16)
                        if (isOperation(charTemp) &&
                                (operationPrior(charIn) <= operationPrior(charTemp))) {
                            strOut += " " + charTemp + " ";
                            strStack = strStack.substring(0, strStack.length() - 1);
                        } else {
                            strOut += " ";
                            break;
                        }
                    }
                }
                strOut += " ";
                strStack += charIn;
            } else if ('(' == charIn) {
                strStack += charIn;
            } else if (')' == charIn) {
                while (strStack.charAt(strStack.length() - 1) != '(') {
                    strOut += " " + strStack.charAt(strStack.length() - 1);
                    strStack = strStack.substring(0, strStack.length() - 1);
                    if (strStack.length() == 0) {
                        throw new Exception("Неверное количество открытых и закрытых скобок");
                    }
                }
                strStack = strStack.substring(0, strStack.length() - 1);

                //TODO тута вклепать проверку на унарный минус

            } else {
                strOut += charIn;
            }
        }
        while (strStack.length() > 0) {
            strOut += " " + strStack.substring(strStack.length() - 1);
            strStack = strStack.substring(0, strStack.length() - 1);
        }
        return  strOut;
    }

    /**
     * Функция говорт, является ли текущий символ оператором, или частью числа
     */
    private static boolean isOperation(char c) {
        switch (c) {
            case '!':
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

    /**
     * Возвращает приоритет операции
     * @param op char
     * @return byte
     */
    private static byte operationPrior(char op) {
        switch (op) {
            case '^':
                return 3;
            case '*':
            case '/':
            case '%':
                return 2;
        }
        return 1; // Тут остается + и -
    }

    /**
     * Считает выражение, записанное в обратной польской нотации
     * @param sIn
     * @return double result
     */
    public static double calculate(String sIn) throws Exception {


        double dLeft = 0, dRight = 0;
        String strTmp;
        Stack<Double> stack = new Stack <>();
        StringTokenizer strToken = new StringTokenizer(sIn);
        while(strToken.hasMoreTokens()) {
            try {
                strTmp = strToken.nextToken().trim();
                if (1 == strTmp.length() && isOperation(strTmp.charAt(0))) {
                    if (stack.size() < 2) {
                        throw new Exception("Неверное количество данных в стеке для операции " + strTmp);
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
                throw new Exception("Недопустимый символ в выражении");
            }
        }
        if (stack.size() > 1) {
            throw new Exception("Количество операторов не соответствует количеству операндов");
        }
        return stack.pop();
    }
}
