import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println(new Date(1612749600106l));
        System.out.println("started.");
        System.out.println(Collections.<Package>emptyList().stream().map(Package::getName).collect(Collectors.toList()));
        int read = System.in.read();
        System.out.println(read);
    }
}
