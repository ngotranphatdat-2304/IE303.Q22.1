import java.util.Random;

class bai2 {
    public static void main(String[] args) {
        int banKinh = 1;

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
        System.out.println("Dien tich hinh tron r = 1 (Monte Carlo): " + dienTichMonteCarlo);
        double pi = dienTichMonteCarlo / (banKinh * banKinh);
        System.out.println("Gia tri xap xi PI: " + pi);
    }
}
