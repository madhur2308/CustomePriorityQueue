import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

class CustomPriorityQueueV2<T> {

    private Map<Integer, PQNode<T>> sortedMap;
    private List<Integer> throttleIndexManager;
    private AtomicInteger top;
    
    public void CustomPriorityQueueV2(int size) {
        this.sortedMap = Collections.synchronizedMap(new TreeMap<>());
        this.throttleIndexManager = Collections.synchronizedList(new ArrayList<>());
        this.top = new AtomicInteger(0);
    }

    public synchronized T dequeue() {
        if (throttleIndexManager.peek() == null) {

        } else {

        }
    }
} 