import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        // check number of arguments
        if (args.length != 2) {
            System.out.println("Wrong number of arguments!");
            return;
        }

        // create output files
        File outFileProducts = new File("order_products_out.txt");
        File outFileOrders = new File("orders_out.txt");

        // if already created, erase the text
        PrintWriter writer = new PrintWriter(outFileProducts);
        writer.print("");
        writer.close();

        writer = new PrintWriter(outFileOrders);
        writer.print("");
        writer.close();

        // get the source folder and the number of threads from the command line arguments
        String folder = args[0];
        int numberThreads = Integer.parseInt(args[1]);

        // get the input files
        String fileOrders = folder + '/' + "orders.txt";
        String fileProducts = folder + '/' + "order_products.txt";

        // initiate reader for the orders input file
        BufferedReader reader = new BufferedReader(new FileReader(fileOrders));
        // create semaphore accepting given number of threads
        Semaphore semaphore = new Semaphore(numberThreads);
        // initiate executor service
        ExecutorService tpe = Executors.newFixedThreadPool(numberThreads);

        // create and start order threads
        ArrayList<Thread> arrayThreads = new ArrayList<>();
        for (int i = 0; i < numberThreads; i++) {
            arrayThreads.add(new ThreadOrders(i, reader, semaphore,
                    fileProducts, outFileProducts, outFileOrders, tpe));
            arrayThreads.get(i).start();
        }

        // run the order threads
        for (int i = 0; i < numberThreads; i++) {
            try {
                arrayThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // close the reader
        reader.close();
    }
}
