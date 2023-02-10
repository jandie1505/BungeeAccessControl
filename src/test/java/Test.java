import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {
        int input = 86399;
        int numberOfDays = input / 86400;
        int numberOfHours = (input % 86400) / 3600 ;
        int numberOfMinutes = ((input % 86400) % 3600) / 60;
        int numberOfSeconds = ((input % 86400) % 3600) % 60;

        System.out.println(input + " " + numberOfDays + " " + numberOfHours + " " + numberOfMinutes + " " + numberOfSeconds);
    }
}
