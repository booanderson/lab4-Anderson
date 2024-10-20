import java.rmi.Naming;

public class client {
    public static void main(String[] args) {
        try {
	    //finding the remote process object in the rmi regsitry
            Process process = (Process) Naming.lookup("rmi://localhost/Process");

            //simulating the first process requesting access to CS
            int processId = 1;
	    //using seconds for time stamps
            int timestamp = (int) (System.currentTimeMillis() / 1000);
	     
            System.out.println("Process " + processId + " requesting critical section at " + timestamp);
            process.requestCriticalSection(processId, timestamp);
            
            // Simulate some time before the next request
            Thread.sleep(5000);

	    //simulating second process requesting the CS
            processId = 2; 
            timestamp = (int) (System.currentTimeMillis() / 1000);
            System.out.println("Process " + processId + " requesting critical section at " + timestamp);
            process.requestCriticalSection(processId, timestamp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

