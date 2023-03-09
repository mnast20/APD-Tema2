import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableProducts implements Runnable {
    int index;
    int numberProducts;
    // final BufferedReader reader;
    String fileProducts;
    File outFileProducts;
    ExecutorService tpe;
    String order;
    final AtomicInteger statusOrder;

    /**
     * Runnable Products constructor
     * @param index indes of order to be searched
     * @param order order to be searched
     * @param numberProducts number of products to be  searched
     * @param fileProducts the products input file
     * @param outFileProducts the products output file
     * @param tpe service executor
     * @param statusOrder the number of found products
     */
    public RunnableProducts(int index, String order, int numberProducts,
                            String fileProducts, File outFileProducts,
                            ExecutorService tpe, AtomicInteger statusOrder) {
        this.index = index;
        this.numberProducts = numberProducts;
        this.fileProducts = fileProducts;
        this.outFileProducts = outFileProducts;
        this.tpe = tpe;
        this.order = order;
        this.statusOrder = statusOrder;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            // read next line
            reader = new BufferedReader(new FileReader(fileProducts));
            int found = 0;
            String line;

            while (true) {
                try {
                    line = reader.readLine();

                    if (line != null) {
                        // get the order and the product from the line
                        String[] stringArray = line.split(",", 2);
                        String comm = stringArray[0];
                        String product = stringArray[1];

                        // check if the order matches
                        if (comm.compareTo(order) == 0) {
                            // increment the number of times the order was found in file
                            found++;

                            // check if order was found a number of times equal to the task's index
                            if (found == index) {
                                // increment the number of shipped products
                                statusOrder.incrementAndGet();

                                FileWriter writer;
                                BufferedWriter bufferedWriter;
                                try {
                                    // write to products output file
                                    writer = new FileWriter(outFileProducts, true);
                                    bufferedWriter = new BufferedWriter(writer);

                                    bufferedWriter.write(order + ',' + product + ',' + "shipped");
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                    reader.close();

                                    // check if the number of delivered products is
                                    // equal to order's number of products
                                    if (statusOrder.get() == numberProducts) {
                                        synchronized (statusOrder) {
                                            statusOrder.notify();
                                        }
                                    }
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        // end of file reached
                        reader.close();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
