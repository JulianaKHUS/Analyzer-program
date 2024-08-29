import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        Thread textGeneratorThread = new Thread(new TextGenerator());
        Thread countThreadA = new Thread(new CountThread('a', queueA));
        Thread countThreadB = new Thread(new CountThread('b', queueB));
        Thread countThreadC = new Thread(new CountThread('c', queueC));

        textGeneratorThread.start();
        countThreadA.start();
        countThreadB.start();
        countThreadC.start();
    }

    static class TextGenerator implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 100000; j++) {
                    sb.append("abc".charAt((int) (Math.random() * 3)));
                }
                String text = sb.toString();
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class CountThread implements Runnable {
        private final char symbol;
        private final BlockingQueue<String> queue;

        public CountThread(char symbol, BlockingQueue<String> queue) {
            this.symbol = symbol;
            this.queue = queue;
        }

        @Override
        public void run() {
            int maxCount = 0;
            String maxText = "";

            while (true) {
                try {
                    String text = queue.take();
                    int count = 0;
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) == symbol) {
                            count++;
                        }
                    }
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
