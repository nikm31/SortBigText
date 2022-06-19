import impl.SorterByPriorityQueueImpl;
import impl.TextGeneratorImpl;
import interfaces.Sorter;
import interfaces.TextGenerator;

public class Main {
    public static void main(String[] args) {
        TextGenerator textGenerator = new TextGeneratorImpl(10L,128849000L);
        Sorter sorter = new SorterByPriorityQueueImpl(textGenerator.generateTextParts());
        sorter.sort();
    }
}
