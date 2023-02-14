import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class CustomPriorityQueueTest {

  private CustomPriorityQueue<Integer> customPriorityQueue;

  @Before
  public void setUp() {
    customPriorityQueue = new CustomPriorityQueue<>(6);
  }

  @Test
  public void testShouldCreatePriorityQueueAndPeekReturnsTopElement() {
    customPriorityQueue.insert(4, 4);
    customPriorityQueue.insert(1, 1);
    customPriorityQueue.insert(2, 2);
    customPriorityQueue.insert(3, 3);

    final String sb = String.valueOf(customPriorityQueue.remove()) +
        customPriorityQueue.remove() +
        customPriorityQueue.remove() +
        customPriorityQueue.remove();

    assertEquals("1234", sb);
  }

  @Test
  public void testShouldCreatePriorityQueueWithThrottleActivated() {
    customPriorityQueue.insert(4, 4);
    customPriorityQueue.insert(1, 1);
    customPriorityQueue.insert(3, 3);
    customPriorityQueue.insert(2, 2);
    customPriorityQueue.insert(1, 1);
    customPriorityQueue.insert(2, 2);

    assertEquals(1, customPriorityQueue.remove().intValue());
    assertEquals(1, customPriorityQueue.remove().intValue());

    customPriorityQueue.insert(1, 1);
    assertEquals(2, customPriorityQueue.remove().intValue());
    assertEquals(1, customPriorityQueue.remove().intValue());
    assertEquals(2, customPriorityQueue.remove().intValue());
    assertEquals(3, customPriorityQueue.remove().intValue());
  }

  @Test
  public void testShouldCreatePriorityQueueWithThrottleActivated2() {
    customPriorityQueue.insert(4, 4);
    customPriorityQueue.insert(1, 1);
    customPriorityQueue.insert(3, 3);
    customPriorityQueue.insert(2, 2);
    customPriorityQueue.insert(2, 2);

    assertEquals(1, customPriorityQueue.remove().intValue());
    assertEquals(2, customPriorityQueue.remove().intValue());

    customPriorityQueue.insert(1, 1);
    assertEquals(1, customPriorityQueue.remove().intValue());
    assertEquals(2, customPriorityQueue.remove().intValue());
    assertEquals(3, customPriorityQueue.remove().intValue());
    assertEquals(4, customPriorityQueue.remove().intValue());
  }

  // 4 1 3 2 1 4 2 3 2 4 1 3 3 5 2 1 3 6 1 2 4 2 4 1 3 2 1 5 2 1 1 2 3 1 1
  @Test
  public void testShouldCreatePriorityQueueWithThrottleActivated3() {
    List<Integer> list =
        Arrays.asList(4, 1, 3, 2, 1, 4, 2, 3, 2, 4, 1, 3, 3, 5, 2, 1, 3, 6, 1, 2, 4, 2, 4, 1, 3, 2,
            1, 5, 2, 1, 1, 2, 3, 1, 1);

    customPriorityQueue = new CustomPriorityQueue<>(list.size());
    for (int num : list) {
      customPriorityQueue.insert(num, num);
    }
    StringBuilder sb = new StringBuilder();
    while (customPriorityQueue.peek() != null) {
      sb.append(customPriorityQueue.remove()).append(",");
    }
    assertEquals("1,1,2,1,1,2,3,1,1,2,1,1,2,3,4,1,1,2,1,2,3,2,2,3,4,5,2,3,3,4,3,4,5,6,4,",
        sb.toString());
  }

  @Test
  public void testPeekShouldReturnNullWhenPQIsEmpty() {
    customPriorityQueue = new CustomPriorityQueue<>(1);
    assertNull(customPriorityQueue.peek());
  }

}
