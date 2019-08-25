package com.datastructures.stacks;

import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class EqualStacks {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n1 = in.nextInt();
        int n2 = in.nextInt();
        int n3 = in.nextInt();

        Container[] containers = new Container[3];
        containers[0] = new Container(n1);
        containers[1] = new Container(n2);
        containers[2] = new Container(n3);

        for (int h1_i = 0; h1_i < n1; h1_i++) {
            containers[0].add(in.nextInt());
        }
        for (int h2_i = 0; h2_i < n2; h2_i++) {
            containers[1].add(in.nextInt());
        }
        for (int h3_i = 0; h3_i < n3; h3_i++) {
            containers[2].add(in.nextInt());
        }
        long lowest = Integer.MAX_VALUE;
        for (Container cont : containers) {
            Collections.reverse(cont.numbers);
            if (lowest > cont.count) {
                lowest = cont.count;
            }
        }
        System.out.println(processContainers(containers, lowest));
        in.close();
    }

    private static long processContainers(Container[] containers, long lowest) {
        while (!isEqual(containers)) {
            for (Container container : containers) {
                lowest = findEquals(container, lowest);
            }
        }
        return lowest;
    }

    private static long findEquals(Container container, long lowest) {
        while (container.count > lowest) {
            container.remove();
        }
        if (container.numbers.isEmpty()) {
            return 0;
        }
        return Math.min(container.count, lowest);
    }

    private static boolean isEqual(Container[] containers) {
        for (int i = 0; i < containers.length - 1; i++) {
			if (containers[i].count != containers[i + 1].count) {
				return false;
			}
        }
        return true;
    }

    static class Container {
        long count;
        Stack<Integer> numbers;

        public Container(int size) {
            numbers = new Stack<Integer>();
        }

        public void add(int item) {
            count += item;
            numbers.push(item);
        }

        public void remove() {
			if (numbers.isEmpty()) {
				return;
			}
            count -= numbers.pop();
        }
    }
}
