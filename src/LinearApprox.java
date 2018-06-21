import org.jfree.ui.RefineryUtilities;

import java.awt.geom.Point2D;
import java.util.*;

import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.POSITIVE_INFINITY;

public class LinearApprox {
    //Общий вид функции: y = a * x + b
    //Программа будет представлять набор данных именно через функцию такого вида
    ArrayList<Point2D.Float>  values; //Набор точек дискретной функции
    float a,b; //Коэффициенты уравнения
    LinearApprox(){
        values = new ArrayList<>();
    }
    public void setValues(ArrayList<Point2D.Float> v){
        values = new ArrayList<>(v);
    }

    public float[] calculate(){
        float summX = 0, summY = 0,summXsq = 0,summXY = 0;
        int n = values.size();
        for (int i = 0; i < values.size(); ++i){
            summX += values.get(i).x;
            summY += values.get(i).y;
            summXsq += Math.pow(values.get(i).x,2);
            summXY += values.get(i).x * values.get(i).y;
        }
            a = (n * summXY - (summX * summY)) / (n * summXsq - (float) Math.pow(summX, 2.0));
            b = (summY - a * summX) / n;
        if (a == POSITIVE_INFINITY || a == NEGATIVE_INFINITY || b == POSITIVE_INFINITY || b ==   NEGATIVE_INFINITY ){
            System.out.println("Произошло деление на 0, ответ неверен");
        }
        float[] ans = {a,b,calcError(a,b)};
        return ans;
    }

    private float calcError(float a, float b){
        float averageBase = 0, averageError = 0;
        for (int i=0; i < values.size(); ++i){
            averageBase += values.get(i).y;
        }

        for (int i=0; i < values.size(); ++i){
            averageError+= Math.abs(a*i + b - values.get(i).y);
        }
        return averageError/averageBase;
    }

    public static void main(String[] args){
        Locale.setDefault(new Locale("en", "US"));
        LinearApprox app = new LinearApprox();
        ArrayList<Point2D.Float> values = new ArrayList<>();
        Scanner reader = new Scanner(System.in);
        System.out.print("Выберите режим ввода: поточечный (наберите 'hand') или автоматический(наберите 'auto'): ");
        String response = reader.next();
        if (response.equals("hand")) {
            System.out.print("Введите количество точек функции: ");
            int length = reader.nextInt(); //Ввод количества точек
            System.out.println("Введите пары значений x, y");
            for (int i = 0; i < length; ++i) {
                values.add(new Point2D.Float());
                System.out.print("Точка №" + String.valueOf(i + 1) + " ");
                try {
                    values.get(i).x = CheckFloat(reader); //Обработка ввода пользователя
                    values.get(i).y = CheckFloat(reader);
                } catch (MyException e) {
                    System.out.println(e.toString());
                }
            }

            app.setValues(values);
        }
        else if (response.equals("auto")){
            System.out.print("Введите коэффициенты a, b, длину выборки n и процент случайности (0-100): ");
            float a,b;
            try {
                a = CheckFloat(reader);
                b = CheckFloat(reader);
                int n = reader.nextInt();
                app.functionSetup(a,b,n,15);
            }
            catch (MyException e){
                System.out.println(e.toString());
            }
        }
        float[] res = app.calculate();
        System.out.print("Функция имеет вид: " + String.valueOf(res[0]) +"*x");
        if (res[1] > 0)
            System.out.println(" + " + String.valueOf(res[1]));
        else
            System.out.println(" " + String.valueOf(res[1]));
        System.out.println("Коэффициент отклонения: " + String.valueOf(res[2] * 100)+"%");
        app.printValues();
        PlotPane a = new PlotPane("Correlation");
        a.addData("Base",app.values);
        a.addData("Approxed",app.CreateApproximateValues());
        a.pack();
        a.plot("Compare");
        RefineryUtilities.centerFrameOnScreen(a);
        a.setVisible(true);
    }

    public void functionSetup(float a, float b, int n, int distortion){
        //Создает исходные данные, искажая функцию y = ax + b случайными отклонениями
        //Distortion - определяет искажение значения, значение будет домножено на [1-distortion;1+distortion]
        float coeff;
        if (distortion > 100)
            distortion = 100;
        Random random = new Random();
        for (int i = 0; i < n; ++i ){
            values.add(new Point2D.Float());
            values.get(i).x = i;
            coeff = ((float)(100 - distortion + random.nextInt(2*distortion)))/100;
            values.get(i).y = (a*i + b) * coeff; //Домножается на случайную величину в пределах 0.85 - 1.15
        }
    }

    public void printValues(){
        System.out.print("X: [ ");
        for (int i = 0; i <  values.size(); ++i)
            System.out.print(String.valueOf(values.get(i).x) + " " );
        System.out.print("]");
        System.out.print("\nY: [ ");
        for (int i = 0; i <  values.size(); ++i)
            System.out.print(String.valueOf(values.get(i).y) + " ");
        System.out.print("]");
    }

    private static float CheckFloat(Scanner sc) throws MyException {
        try {
            //float input = sc.nextFloat();
            if (!sc.hasNextFloat()) {
                throw new MyException("Wrong type");
            } else {
                return sc.nextFloat();
            }
        } catch (InputMismatchException ime) {
            sc.next(); // Очистка сканера (пропуск значения)
            // Кидаем исключение
            throw new MyException("Wrong type");
        }
    }

    public ArrayList<Point2D.Float> CreateApproximateValues(){
        ArrayList<Point2D.Float> approxValues = (ArrayList<Point2D.Float>) values.clone();
        for (int i = 0; i < values.size(); ++i){
            approxValues.get(i).y = a * approxValues.get(i).x + b;
        }
        return approxValues;
    }

    private static class MyException extends Exception {
        //Ошибка определения типа
        public MyException(String message) {
            super(message);
        }
    }
}
