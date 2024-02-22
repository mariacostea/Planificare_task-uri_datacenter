/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {

    public AtomicInteger host = new AtomicInteger();

   public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if (this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) 
        {
            // se adauga taskul la hostul sugerat prin formula din cerinta
            hosts.get(host.getAndIncrement() % hosts.size()).addTask(task);
            
        } else if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) 
        {
            // se cauta hostul cu coada de taskuri cea mai mica
            int min = hosts.get(0).getQueueSize();
            host.set(0);
              for (int i = 1; i < hosts.size(); i++) {
                  if (hosts.get(i).getQueueSize() < min) {
                      min = hosts.get(i).getQueueSize();
                      host.set(i);
                  }
              }
            // se adauga taskul la hostul corespunzator 
            hosts.get(host.get()).addTask(task);
        } else if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) 
        {
            // se aduaga taskul la hostul corespunzator dupa marimee
            if (task.getType() == TaskType.SHORT) {
                this.hosts.get(0).addTask(task);
            } else if (task.getType() == TaskType.MEDIUM) {
                this.hosts.get(1).addTask(task);
            } else if (task.getType() == TaskType.LONG) {
                this.hosts.get(2).addTask(task);
            }
        } else if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) 
        {
            // se cauta hostul cel mai putin timp ramas de executat
            long min = hosts.get(0).getWorkLeft();
            host.set(0);
            for (int i = 1; i < hosts.size(); i++) {
                if (hosts.get(i).getWorkLeft() < min) {
                    min = hosts.get(i).getWorkLeft();
                    host.set(i);
                }
            }
            // se adauga taskul la hostul corespunzator
            hosts.get(host.get()).addTask(task);
        } 
    }
}
