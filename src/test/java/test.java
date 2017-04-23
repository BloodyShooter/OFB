import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(12);
        list.add(2);
        list.add(101);
        list.add(20);
        list.add(27);
        list.add(40);
        list.add(87);

        System.out.println("Четные числа, которые больше 10");
        list.stream()
                .filter(integer -> integer % 2 == 0)
                .filter(integer -> integer > 10)
                .forEach(integer -> System.out.print(integer + ":"));

        System.out.println();
        Optional<Integer> maxVal = list.stream().max(Integer::compare);
        maxVal.ifPresent(integer -> System.out.println("Максимальное значение: " + integer));

        list.stream().parallel().min(Integer::compare)
                .ifPresent(integer -> System.out.println("Минимальное значение: " + integer));

        System.out.println("Последовательные потоки");
        Stream<Integer> stream = list.stream();
        stream.forEach(integer -> System.out.print(integer + ": "));

        System.out.println();
        System.out.println("Паралельные потоки");
        stream = list.stream();
        stream.parallel().forEach(integer -> System.out.print(integer + ": "));
    }
}
