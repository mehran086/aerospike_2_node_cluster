import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;

public class AerospikeTest {
    public static void main(String[] args) throws InterruptedException {
        ClientPolicy policy = new ClientPolicy();
        policy.useServicesAlternate = true; // <-- critical
        AerospikeClient client = new AerospikeClient(policy, "127.0.0.1", 9000);

        Key key = new Key("test", "demo", "user1");
        Bin bin1 = new Bin("name", "Mehran");
        client.put(null, key, bin1);
        System.out.println("Inserted: " + bin1.value);

        // Wait for replication
        Thread.sleep(3000);

//        AerospikeClient clientNode2 = new AerospikeClient("localhost", 4000);
        AerospikeClient clientNode2 = new AerospikeClient(policy, "127.0.0.1", 9001);
        AerospikeClient clientNode3 = new AerospikeClient(policy, "127.0.0.1", 9002);
        Record record = clientNode3.get(null, key);
//        Record record = client.get(null, key);

        if (record != null) {
            System.out.println("Read from node2: " + record.getString("name"));
        } else {
            System.out.println("Record not found on node2 yet!");
        }

        client.close();
        clientNode2.close();
    }
}
