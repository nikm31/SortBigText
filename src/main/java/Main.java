import impl.SorterImpl;
import impl.TextGeneratorImpl;
import interfaces.Sorter;
import interfaces.TextGenerator;

public class Main {
    public static void main(String[] args) {
        TextGenerator textGenerator = new TextGeneratorImpl(10L,3000000L);
        Sorter sorter = new SorterImpl(textGenerator.generateTextParts());
        sorter.sort();
    }
}
