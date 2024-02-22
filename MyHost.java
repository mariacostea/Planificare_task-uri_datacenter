/* Implement this class. */

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MyHost extends Host {


    private boolean ok = true;

    //coada de prioritate care contine si taskurile care se afla in rulare si in asteptare
    private PriorityQueue<Task> alltasks = new PriorityQueue<Task>(10,
        (t1, t2) -> {
            if (t1.getPriority() == t2.getPriority()) {
                return t1.getId() - t2.getId();
            } 
            return t2.getPriority() - t1.getPriority();
           });

    //coada de prioritate care contine doar taskurile care se afla in asteptare       
    private BlockingQueue<Task> tasks = new PriorityBlockingQueue<Task>(10,
        (t1, t2) -> {
            if (t1.getPriority() == t2.getPriority()) {
                return t1.getId() - t2.getId();
            } 
            return t2.getPriority() - t1.getPriority();
           });
        
    Task task;
    Task aux;
    
    @Override
    public void run() {
        while (ok) {

            // se verifica daca exista taskuri in coada de prioritati
            if (tasks.size() > 0) {
            	try {
            		task = tasks.take();
            	} catch (InterruptedException e) {
            		e.printStackTrace();
            	}
            	
                //se verifica daca taskul este preemptibil
            	if (tasks.peek() != null && task.isPreemptible()) {
            		aux = tasks.peek();

                    //daca exista alt task in coada cu prioritate mai mare se ruleaza acela in locul celui preemtibil
            		if (task.getPriority() < aux.getPriority() && task.getLeft() > 0) {
            			try {
            				aux = tasks.take();
            			} catch (InterruptedException e) {
            				e.printStackTrace();
            			}
            			
            			addTask(task);
            			task = aux;
            		}
            	}
            	
            	// se verifica daca taskul este non-preemptibil
            	if (tasks.peek() != null) {
	             	aux = tasks.peek();

                    //se verifica daca un task nepreemptibil se afla in rulare
                    //se asigura ca executia lui nu va fi intrerupta de un task cu prioritate mai mare
	             	if (!aux.isPreemptible() && aux.getPriority() < task.getPriority() && aux.getLeft() > 0 && aux.getLeft() < aux.getDuration()) {
	        	 		try {
	        	 			aux = tasks.take();
	        	 		} catch (InterruptedException e) {
	        	 			e.printStackTrace();
	        	 		}
	        	 		addTask(task);
	        	 		task = aux;
                        
	        	 	}
            	}
            
                //se verifica daca taskul este null
                if (task != null) {

                    //se verifica daca taskul este in asteptare
                    synchronized (task) {
                        try {
                            //se asteapta timpul necesar pentru a rula taskul
                            Thread.sleep(1000);
                            task.setLeft(task.getLeft() - 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //se verifica daca taskul si-a terminat rularea
                        if (task.getLeft() > 0) {
                            addTask(task);
                        } else if (task.getLeft() <= 0){
                            //se elimina taskul din coada cu toate taskurile in astepare si rulare
                            alltasks.remove(task);
                            task.finish();
                        }
                    }
                }
            }    
        }
    }

    @Override
    public void addTask(Task task) {
    	try {
    		tasks.put(task);
            //se verifica daca taskul nu se afla deja in coada cu toate taskurile in asteptare si rulare
            if (!alltasks.contains(task)) {
                alltasks.add(task);
            }
    	} catch(InterruptedException e) {
    		e.printStackTrace();
    	}
    }

    @Override
    public int getQueueSize() {
        return alltasks.size();
    }

    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        //se calculeaza timpul ramas pentru toate taskurile din coada
        for (Task task : alltasks) {
            workLeft += task.getLeft();
        }
        return workLeft;
    }

    @Override
    public void shutdown() {
        interrupt();
        ok = false;

    }
}