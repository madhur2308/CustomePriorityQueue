public class Main {
  public static void main(String[] args) {
    // Example 1
    CustomPriorityQueue<Integer> solution = new CustomPriorityQueue<>(6);
    solution.insert(4, 4);
    solution.insert(1, 1);
    solution.insert(3, 3);
    solution.insert(2, 2);
    solution.insert(1, 1);
    solution.insert(2, 2);


    System.out.println(solution.remove());
    System.out.println(solution.remove());
    solution.insert(1, 1);
    System.out.println(solution.remove());
    System.out.println(solution.remove());
    System.out.println(solution.remove());
    System.out.println(solution.remove());
  }
}