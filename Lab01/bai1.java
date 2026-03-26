import java.util.Scanner;
import java.util.Random;

class bai1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double banKinh = sc.nextDouble();
        double dienTichPI = Math.PI * banKinh * banKinh;
        int n = 10000000;
        int count = 0;
        Random rd = new Random();
        for (int i = 0; i < n; i++) {
            double x = rd.nextDouble() *banKinh;
            double y = rd.nextDouble() *banKinh;
            if (x * x + y * y <= banKinh * banKinh) {
                count++;
            }
        }

        double dienTichMonteCarlo = (double) count / n * (banKinh * banKinh) * 4;
        System.out.println("Dien tich hinh tron (PI): " + dienTichPI);
        System.out.println("Dien tich hinh tron (Monte Carlo): " + dienTichMonteCarlo);
        sc.close();
    }
}