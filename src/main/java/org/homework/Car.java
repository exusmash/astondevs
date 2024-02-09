package org.homework;

public class Car {
    private int year;
    private String model;

    @Override
    public String toString() {
        return "Car{" +
                "year=" + year +
                ", model='" + model + '\'' +
                '}';
    }

    public Car(int year, String model) {
        this.year = year;
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

//    public static void main(String[] args) {
//        MyArrayList<Car> myArrayList = new MyArrayList<>();
//
//        myArrayList.add(new Car(2005, "Volvo"));
//        myArrayList.add(new Car(2001, "BMW"));
//        myArrayList.add(new Car(2003, "Mercedes"));
//        myArrayList.add(new Car(2002, "Audi"));
//        myArrayList.add(new Car(2004, "Suzuki"));
//        myArrayList.print();
//
//        myArrayList.sort((car1, car2) -> {
//            if (car1.getYear() == car2.getYear()) return 0;
//            if (car1.getYear() < car2.getYear()) return -1;
//            return 1;
//        });
//
//        myArrayList.print();
//    }
}
