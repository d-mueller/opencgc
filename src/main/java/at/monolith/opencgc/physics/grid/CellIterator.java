package at.monolith.opencgc.physics.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple multi-threaded cell iterator. Executes cell action using multiple threads on all cells in the grid.
 */
public class CellIterator
{
    public Grid grid;
    public int[] numberOfCells;
    public int totalNumberOfCells;
    public int numberOfThreads;
    public CellAction action;
    private List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
    private ExecutorService threadExecutor;


    public CellIterator(Grid grid)
    {
        this.numberOfCells = grid.numberOfCells;
        this.totalNumberOfCells = grid.totalNumberOfCells;
        this.grid = grid;
        this.numberOfThreads = 4;
        this.threadExecutor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; ++i)
        {
            tasks.add(new Task(i, numberOfThreads));
        }
    }

    public void execute(CellAction action)
    {
        this.action = action;
        try
        {
            threadExecutor.invokeAll(tasks);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    class Task implements Callable<Object>
    {

        private int threadIdx;
        private int numOfThreads;

        private Task(int threadIdx, int numOfThreads)
        {
            this.threadIdx = threadIdx;
            this.numOfThreads = numOfThreads;
        }

        public Object call() throws Exception
        {
            for (int cellIdx = threadIdx; cellIdx < totalNumberOfCells; cellIdx += numOfThreads)
            {
                action.execute(grid, cellIdx);
            }
            return null;
        }
    }
}
