import org.homework.MyArrayList;
import org.junit.Test;


import java.util.Comparator;

import static org.junit.Assert.*;

public class MyArrayListTests {

    @Test
    public void testAddAndGet() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        assertEquals(2, (int) list.get(1));
    }

    @Test
    public void testAddAtIndex() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("apple");
        list.add("orange");
        list.add("banana", 1);
        assertEquals("banana", list.get(1));
    }

    @Test
    public void testRemoveValidIndex() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("apple");
        list.add("orange");
        list.add("banana");

        list.remove(1); // Удалить элемент с индексом 1

        assertEquals(2, list.size()); // Проверить, что размер уменьшился
        assertEquals("apple", list.get(0)); // Проверить, что первый элемент остался неизменным
        assertEquals("banana", list.get(1)); // Проверить, что последний элемент сдвинулся
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveInvalidIndex() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        list.remove(2); // Попытка удалить элемент с недопустимым индексом
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testClear() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        list.clear();

        assertEquals(0, list.size()); // Проверить, что размер стал равным 0
        assertNull(list.get(0)); // Проверить, что первый элемент стал равным null
        assertNull(list.get(1)); // Проверить, что второй элемент стал равным null
        assertNull(list.get(2)); // Проверить, что третий элемент стал равным null
    }

    @Test
    public void testSortNaturalOrder() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        list.sort();

        assertEquals(1, (int) list.get(0)); // Проверить, что элементы отсортированы в естественном порядке
        assertEquals(2, (int) list.get(1));
        assertEquals(3, (int) list.get(2));
    }

    @Test
    public void testSortWithComparator() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("banana");
        list.add("orange");
        list.add("apple");

        Comparator<String> reverseComparator = Comparator.reverseOrder();
        list.sort(reverseComparator);

        assertEquals("orange", list.get(0)); // Проверить, что элементы отсортированы в обратном порядке
        assertEquals("banana", list.get(1));
        assertEquals("apple", list.get(2));
    }
}
