import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomPriorityQueue<T> {

  private final AtomicInteger lastDequedPriority = new AtomicInteger(0);
  private final AtomicInteger elementCount = new AtomicInteger(0);

  private final AtomicInteger priorityArrayElementCount = new AtomicInteger(0);

  private AtomicInteger THROTTLE_RATE = new AtomicInteger(2);

  private final AtomicInteger counterToReachThrottleLimit = new AtomicInteger(0);

  private final Map<Integer, List<T>> priorityValueMap = new ConcurrentHashMap<>();

  private final AtomicInteger[] arr;

  CustomPriorityQueue(int size) {
    // we create the array of size + 1, so it becomes easier to work with the heap creation
    arr = new AtomicInteger[size + 1];
  }

  CustomPriorityQueue(int size, int throttleRate) {
    // we create the array of size + 1, so it becomes easier to work with the heap creation
    arr = new AtomicInteger[size + 1];
    this.THROTTLE_RATE = new AtomicInteger(throttleRate);
  }

  public synchronized boolean insert(T element, int priority) {
    if (elementCount.get() == arr.length - 1) {
      return false;
    }
    priorityValueMap.computeIfPresent(priority, (k, v) -> {
      v.add(element);
      elementCount.addAndGet(1);
      return v;
    });
    if (!priorityValueMap.containsKey(priority)) {
      elementCount.addAndGet(1);
      insertInPriorityQueue(priority);
      List<T> syncList = Collections.synchronizedList(new ArrayList<>());
      syncList.add(element);
      priorityValueMap.put(priority, syncList);
    }
    return true;
  }

  private synchronized void insertInPriorityQueue(int priority) {
    priorityArrayElementCount.addAndGet(1);
    AtomicBoolean isInsertedInCorrectly = new AtomicBoolean(true);
    arr[priorityArrayElementCount.get()] = new AtomicInteger(priority);
    AtomicInteger index = new AtomicInteger(priorityArrayElementCount.get());
    // todo move this to a helper function (sift down)
    while (isInsertedInCorrectly.get()) {
      if (index.get() / 2 < 1) {
        isInsertedInCorrectly.set(false);
      } else {
        AtomicInteger parent = arr[index.get() / 2];
        AtomicInteger currentValueInserted = arr[index.get()];
        if (parent.get() > currentValueInserted.get()) {
          // swap the values
          arr[index.get() / 2] = currentValueInserted;
          arr[index.get()] = parent;
          index = new AtomicInteger(index.get() / 2);
        } else {
          isInsertedInCorrectly.set(false);
        }
      }
    }
  }

  public synchronized T peek() {
    if (arr[1] == null) {
      return null;
    }
    return Optional.of(priorityValueMap.get(arr[1].get()).get(0)).orElse(null);
  }

  public synchronized T remove() {
    // todo add block until available to remove

    T dequedValue = null;
    if (lastDequedPriority.get() == arr[1].get() &&
        counterToReachThrottleLimit.get() == THROTTLE_RATE.get()) {
      AtomicInteger priorityToReInsert = fixPriorityQueueOrdering();
      dequedValue = removeElementWhenNotThrottled();
      insertInPriorityQueue(priorityToReInsert.get());
      counterToReachThrottleLimit.set(0);
    } else {
      lastDequedPriority.set(arr[1].get());
      dequedValue = removeElementWhenNotThrottled();
      counterToReachThrottleLimit.addAndGet(1);
    }
    elementCount.decrementAndGet();
    return dequedValue;
  }

  private synchronized T removeElementWhenNotThrottled() {
    T dequedValue;
    List<T> list = priorityValueMap.get(arr[1].get());
    dequedValue = list.remove(0);
    if (list.size() < 1) {
      priorityValueMap.remove(arr[1].get());
      fixPriorityQueueOrdering();
    }
    return dequedValue;
  }

  private synchronized AtomicInteger fixPriorityQueueOrdering() {
    AtomicInteger dequeudPriority = arr[1];
    arr[1] = arr[priorityArrayElementCount.get()];
    arr[priorityArrayElementCount.get()] = null;
    priorityArrayElementCount.decrementAndGet();
    AtomicInteger index = new AtomicInteger(1);
    while (index != null) {
      AtomicInteger child1Index = new AtomicInteger(2 * (index.get()));
      AtomicInteger child1 = child1Index.get() < arr.length ? arr[child1Index.get()] : null;

      AtomicInteger child2Index = new AtomicInteger((2 * index.get()) + 1);
      AtomicInteger child2 = child2Index.get() < arr.length ? arr[child2Index.get()] : null;

      index = swapMinChildWithParent(index, child1, child1Index, child2, child2Index);
    }
    return dequeudPriority;
  }

  private synchronized AtomicInteger swapMinChildWithParent(AtomicInteger parentIndex,
                                                            AtomicInteger child1,
                                                            AtomicInteger child1Index,
                                                            AtomicInteger child2,
                                                            AtomicInteger child2Index) {
    AtomicInteger minChildIndex;

    if (child1 == null && child2 == null) {
      return null;
    } else if (child2 == null) {
      minChildIndex = child1Index;
    } else if (child1 == null) {
      minChildIndex = child2Index;
    } else {
      minChildIndex =
          child2.get() >= child1.get() ? child1Index : child2Index;
    }
    if (arr[parentIndex.get()].get() > arr[minChildIndex.get()].get()) {
      AtomicInteger swap = arr[parentIndex.get()];
      arr[parentIndex.get()] = arr[minChildIndex.get()];
      arr[minChildIndex.get()] = swap;
    } else  {
      return null;
    }
    return minChildIndex;
  }
}
