import java.util.Scanner;

public class TestKlasseAlton {

    // Methode für Addition
    public static double add(double a, double b) {
        return a + b;
    }

    // Methode für Subtraktion
    public static double subtract(double a, double b) {
        return a - b;
    }

    // Methode für Multiplikation
    public static double multiply(double a, double b) {
        return a * b;
    }

    // Methode für Division
    public static double divide(double a, double b) {
        if (b != 0) {
            return a / b;
        } else {
            System.out.println("Fehler! Division durch Null ist nicht erlaubt.");
            return 0;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Taschenrechner");
        System.out.println("---------------");
        System.out.print("Geben Sie die erste Zahl ein: ");
        double num1 = scanner.nextDouble();

        System.out.print("Geben Sie die zweite Zahl ein: ");
        double num2 = scanner.nextDouble();

        System.out.println("Wählen Sie die Operation: ");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraktion (-)");
        System.out.println("3. Multiplikation (*)");
        System.out.println("4. Division (/)");

        int operation = scanner.nextInt();
        double result = 0;

        // Switch-Case für die Auswahl der Operation
        switch (operation) {
            case 1:
                result = add(num1, num2);
                System.out.println("Ergebnis der Addition: " + result);
                break;
            case 2:
                result = subtract(num1, num2);
                System.out.println("Ergebnis der Subtraktion: " + result);
                break;
            case 3:
                result = multiply(num1, num2);
                System.out.println("Ergebnis der Multiplikation: " + result);
                break;
            case 4:
                result = divide(num1, num2);
                System.out.println("Ergebnis der Division: " + result);
                break;
            default:
                System.out.println("Ungültige Operation!");
                break;
        }

        scanner.close();
    }
}
