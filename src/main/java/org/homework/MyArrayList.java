package org.homework;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Класс MyArrayList представляет собой реализацию динамического массива (ArrayList) в Java.
 * Предоставляет методы для добавления, получения, удаления, очистки, сортировки элементов
 * и обеспечения емкости внутреннего массива.
 *
 * @param <T> тип элементов, хранящихся в списке.
 */
public class MyArrayList<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    /**
     * Конструктор для создания пустого объекта MyArrayList с емкостью по умолчанию.
     */
    public MyArrayList() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Добавляет указанный элемент в конец списка.
     *
     * @param elem элемент для добавления.
     */
    public void add(T elem) {
        ensureCapacity();
        elements[size++] = elem;
    }

    /**
     * Добавляет указанный элемент по указанному индексу в списке.
     *
     * @param elem  элемент для добавления.
     * @param index индекс, по которому элемент будет добавлен.
     * @throws IndexOutOfBoundsException если индекс находится вне допустимого диапазона.
     */
    public void add(T elem, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        ensureCapacity();
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = elem;
        size++;
    }

    /**
     * Возвращает элемент по указанному индексу в списке.
     *
     * @param index индекс элемента для получения.
     * @return элемент по указанному индексу.
     * @throws IndexOutOfBoundsException если индекс находится вне допустимого диапазона.
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return (T) elements[index];
    }

    /**
     * Удаляет элемент по указанному индексу из списка.
     *
     * @param index индекс элемента для удаления.
     * @throws IndexOutOfBoundsException если индекс находится вне допустимого диапазона.
     */
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        System.arraycopy(elements, index + 1, elements, index, size - index);
        size--;
    }

    /**
     * Очищает весь список, устанавливая все элементы в null и сбрасывая размер списка.
     */
    public void clear() {
        Arrays.fill(elements, null);
        size = 0;
    }

    /**
     * Сортирует элементы списка в естественном порядке с использованием алгоритма QuickSort.
     */
    public void sort() {
        quickSort(0, size - 1, null);
    }

    /**
     * Сортирует элементы списка с использованием указанного компаратора.
     *
     * @param comparator компаратор для определения порядка сортировки.
     */
    public void sort(Comparator<? super T> comparator) {
        quickSort(0, size - 1, comparator);
    }

    /**
     * Рекурсивно сортирует элементы списка в диапазоне индексов от low до high с использованием алгоритма QuickSort.
     *
     * @param low        нижний индекс диапазона сортировки.
     * @param high       верхний индекс диапазона сортировки.
     * @param comparator компаратор для определения порядка сортировки (null для естественного порядка).
     */
    private void quickSort(int low, int high, Comparator<? super T> comparator) {
        if (low < high) {
            // Выбор опорного элемента и его индекса
            int pivotIndex = partition(low, high, comparator);

            // Рекурсивная сортировка для подсписков до и после опорного элемента
            quickSort(low, pivotIndex - 1, comparator);
            quickSort(pivotIndex + 1, high, comparator);
        }
    }

    /**
     * Выполняет разделение списка на две части относительно опорного элемента, и возвращает индекс опорного элемента.
     *
     * @param low        нижний индекс диапазона разделения.
     * @param high       верхний индекс диапазона разделения.
     * @param comparator компаратор для определения порядка сравнения (null для естественного порядка).
     * @return индекс опорного элемента после завершения процесса разделения.
     */
    private int partition(int low, int high, Comparator<? super T> comparator) {
        // Выбор опорного элемента
        T pivot = (T) elements[high];
        int i = low - 1;

        // Проход по диапазону и обмен элементов, чтобы разделить на две группы
        for (int j = low; j < high; j++) {
            if (compare(elements[j], pivot, comparator) <= 0) {
                i++;
                swap(i, j);
            }
        }

        // Обмен опорного элемента с элементом после группы меньших элементов
        swap(i + 1, high);

        // Возвращение индекса опорного элемента
        return i + 1;
    }

    /**
     * Обменивает значения двух элементов в массиве по их индексам.
     *
     * @param i индекс первого элемента.
     * @param j индекс второго элемента.
     */
    private void swap(int i, int j) {
        Object temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    /**
     * Сравнивает два объекта с использованием компаратора, если он предоставлен,
     * или с использованием естественного порядка, если компаратор равен null.
     *
     * @param obj1       первый объект для сравнения.
     * @param obj2       второй объект для сравнения.
     * @param comparator компаратор для определения порядка сравнения (null для естественного порядка).
     * @return результат сравнения объектов.
     */
    private int compare(Object obj1, Object obj2, Comparator<? super T> comparator) {
        if (comparator == null) {
            return ((Comparable<? super T>) obj1).compareTo((T) obj2);
        } else {
            return comparator.compare((T) obj1, (T) obj2);
        }
    }

    /**
     * Убеждается, что внутренний массив имеет достаточную емкость для добавления нового элемента.
     * Если текущая емкость недостаточна, увеличивает ее в два раза.
     */
    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    /**
     * Печатает все элементы списка в одной строке.
     */
    public void print() {
        for (int i = 0; i < size; i++) {
            System.out.print((T) elements[i] + " ");
        }
        System.out.println();
    }

    public int size() {
        return size;
    }
}
