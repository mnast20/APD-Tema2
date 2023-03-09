import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadOrders extends Thread {
    int id;
    private final BufferedReader reader;
    Semaphore semaphore;
    String fileProducts;
    File outFileProducts, outFileOrders;
    ExecutorService tpe;

    /***
     * ThreadOrders Constructor
     * @param id order thread ID
     * @param reader reader in input file for orders
     * @param semaphore threads semaphore
     * @param fileProducts products input file
     * @param outFileProducts output file for products
     * @param outFileOrders output file for orders
     */
    public ThreadOrders(int id, BufferedReader reader, Semaphore semaphore,
                        String fileProducts, File outFileProducts,
                        File outFileOrders, ExecutorService tpe) {
        this.id = id;
        this.reader = reader;
        this.semaphore = semaphore;
        this.fileProducts = fileProducts;
        this.outFileOrders = outFileOrders;
        this.outFileProducts = outFileProducts;
        // initiate task pool
        this.tpe = tpe;
    }

    public void run() {
        String order;
        int numberProducts;

        String line;
        try {
            while (true) {
                semaphore.acquire();
                // read next line
                line = reader.readLine();

                if (line != null) {
                    // get the order and the number of products
                    String[] stringArray = line.split(",", 2);
                    order = stringArray[0];
                    numberProducts = Integer.parseInt(stringArray[1]);
                } else {
                    // executor service won't accept any more tasks
                    tpe.shutdown();
                    semaphore.release();
                    return;
                }

                // ignore orders with 0 products
                if (numberProducts > 0) {
                    // number of delivered products
                    AtomicInteger statusOrder = new AtomicInteger(0);

                    // submit tasks to thread pool
                    for (int i = 1; i <= numberProducts; i++) {
                        tpe.submit(new RunnableProducts(i, order, numberProducts,
                                                        fileProducts, outFileProducts,
                                                        tpe, statusOrder));
                    }

                    // wait until all the products from the current order were delivered
                    synchronized (statusOrder) {
                        statusOrder.wait();
                    }

                    // check if order was completed
                    if (statusOrder.get() == numberProducts) {
                        FileWriter writer;
                        BufferedWriter bufferedWriter;
                        String status = "shipped";

                        try {
                            // write to orders_out file
                            writer = new FileWriter(outFileOrders, true);
                            bufferedWriter = new BufferedWriter(writer);
                            bufferedWriter.write(order + ',' + numberProducts
                                    + ',' + status);
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                semaphore.release();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
