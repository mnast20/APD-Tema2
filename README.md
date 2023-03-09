# APD-Tema2

After the output files are created, the threads managing the Orders file (level 1 threads) are started and begin their execution. The threads all contain the reference to a buffered reader for the input file for orders. A semaphore is used to allow the given number of threads (value obtained from the command line arguments) to coexist in the critical section. Inside this critical section, a threa reads the next line using the buffered reader, thus getting the order information, and then issues a number of tasks to search for all the products associated with that order. These tasks will be put in the Executor's task pool. Then, the main thread waits for the current order to be completely processed, meaning that all products must be shipped. When a task is being executed by the level 2 threads, the products file is read line by line until the product corresponding to the task's index is found and then, the product's information is put in the respective output file, now having the status "shipped". Once all products were delivered, meaning that all the tasks associated to the current order were resolved, the executor service thread notifies the main thread to resume its execution. It will then write to the output file that the order has been fully shipped. The process will repeat itself until one of the main threads reaches the end of file, thus shutting down the executor service and closing the buffered reader.

## Classes:
### Main class (Tema2):
Class creating the output files, that initiates the buffered reader and starts the main threads (Order threads) and executes them.

### ThreadOrders:
Class using a buffered reader to read each line from the orders input file and submitting tasks based on the found order. The thread waits until the order is fully processed to then write to the output file. The process continues until it reaches the end of file.

### RunnableProducts:
Class using a buffered reader in order to read products input file line by line, searching for the given order located at the index associated with the task. Once it is found, the thread writes to the products output file. If all the products are found and delivered, the main thread gets notified.

