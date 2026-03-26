import java.util.*;

public class bai4 {
    public static void findLongestSubarray(int[] a, int n, int k) {
        int[] dp = new int[k + 1];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        boolean[][] keep = new boolean[n + 1][k + 1];

        for (int i = 1; i <= n; i++) {
            int val = a[i - 1];
            for (int j = k; j >= val; j--) {
                if (dp[j - val] != -1) {
                    if (dp[j - val] + 1 >= dp[j]) {
                        dp[j] = dp[j - val] + 1;
                        keep[i][j] = true;
                    }
                }
            }
        }

        if (dp[k] == -1) {
            System.out.println("Khong co day con co tong bang " + k);
        } else {
            List<Integer> result = new ArrayList<>();
            int tempK = k;
            for (int i = n; i > 0; i--) {
                if (keep[i][tempK]) {
                    result.add(a[i - 1]);
                    tempK -= a[i - 1];
                }
            }
            Collections.reverse(result);
            for (int i = 0; i < result.size(); i++) {
                System.out.print(result.get(i) + (i == result.size() - 1 ? "" : ", "));
            }
            System.out.println("\nDo dai: " + dp[k]);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] A = new int[n];
        for (int i = 0; i < n; i++) {
            A[i] = sc.nextInt();
        }
        for (int num : A) {
            System.out.print(num + " ");
        }
     
        System.out.print("Day con dai nhat co tong bang " + k + " la: ");
        findLongestSubarray(A, n, k);
        sc.close();
    }
}
