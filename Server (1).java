import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//interface to definition of methodds
interface Process extends Remote {
    //method definition to request access to the CS
    void requestCriticalSection(int processId, int timestamp) throws RemoteException;
    //method defintion to release process from CS
    void releaseCriticalSection(int processId) throws RemoteException;
}

//
public class Server extends UnicastRemoteObject implements Process {
    //process variable definition
    private boolean inCriticalSection = false;
    private int quorumSize;
    private List<Integer> quorum;
    //queue to hold the requests to enter CS
    private Queue<int[]> requestQueue = new LinkedList<>(); 
    private int[] votes;

    //setter method for the server class
    public Server(List<Integer> quorum) throws RemoteException {
        this.quorum = quorum;
        this.quorumSize = quorum.size();
        this.votes = new int[quorumSize];
    }

    //method to handle the requests to enter the CS
    public synchronized void requestCriticalSection(int processId, int timestamp) throws RemoteException {
        System.out.println("Received request from process " + processId + " at time " + timestamp);
        requestQueue.add(new int[] { processId, timestamp });
        if (!inCriticalSection && canEnterCriticalSection()) {
            grantRequest(processId);
        }
    }

    //method to release processes from the CS
    public synchronized void releaseCriticalSection(int processId) throws RemoteException {
        System.out.println("Process " + processId + " released critical section");
        inCriticalSection = false;
        if (!requestQueue.isEmpty()) {
            int[] nextRequest = requestQueue.poll();
	    //giving access the enter to the next requesting process
            grantRequest(nextRequest[0]);
        }
    }

    public synchronized void grantRequest(int processId) throws RemoteException {
        System.out.println("Granting request to process " + processId);
        inCriticalSection = true;
        //simulating the work being done in the CS
        new Thread(() -> {
            try {
                Thread.sleep(2000); 
                releaseCriticalSection(processId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); //starting the thread for the CS "work" to be done
    }

    //if number of req. in quorum size is >= quorum size variable, process can enter
    private boolean canEnterCriticalSection() {
        return requestQueue.size() >= quorumSize;
    }

    //the main method to start the server
    public static void main(String[] args) {
        try {
	    //creating the RMI registry within the server instead of running from terminal 
	    Registry registry = LocateRegistry.createRegistry(1099);
            List<Integer> quorum = Arrays.asList(1, 2, 3); // Define quorum set for this server
            //creating an instance of the server
            Server server = new Server(quorum);
	    //binding the server to the rmi registry object
            Naming.rebind("rmi://localhost/Process", server);
            System.out.println("Server is ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

