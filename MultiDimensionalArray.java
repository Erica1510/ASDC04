import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.lang.Runtime;

public class MultiDimensionalArray {
    private final int[] dimensions;
    private final int[][] intervals;
    private final int[] B;
    private final int[][] aliffeVectors;

    public MultiDimensionalArray(int[] dimensions, int[][] intervals) {
        this.dimensions = dimensions;
        this.intervals = intervals;
        int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
        this.B = new int[size];
        this.aliffeVectors = getAliffeVectors();
    }

//    public int getByDeterminant(int... indexes) {
//
//        int goalIndex = 0;
//        for (int i = 0; i < indexes.length; i++) {
//            int product = 1;
//            for (int j = i + 1; j < dimensions.length; j++) {
//                product *= dimensions[j];
//            }
//            if (indexes[i] < intervals[i][0] || indexes[i] > intervals[i][1]) {
//                throw new IndexOutOfBoundsException("Index out of bounds for dimension " + i);
//            }
//
//            goalIndex += (indexes[i] - 1) * product;
//        }
//        goalIndex += indexes[indexes.length - 1];
//        return B[goalIndex];
//    }

    public int directAccess(int... indexes) {
        int goalIndex = indexes[indexes.length - 1];
        goalIndex += IntStream.range(0, indexes.length - 1)
                .map(i -> indexes[i] * Arrays.stream(
                                dimensions, i + 1, indexes.length
                        ).reduce(1, (a, b) -> a * b)
                )
                .sum();
        return B[goalIndex];
    }

    public int getByAliffe(int... indexes) {
        int goalIndex = 0;
        for (int i = 0; i < aliffeVectors.length; i++) {
            goalIndex += aliffeVectors[i][indexes[i]];
        }
        return B[goalIndex];
    }

    private int[][] getAliffeVectors() {
        int[][] vectors = new int[dimensions.length][];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new int[dimensions[i]];
        }
        int interval = B.length;
        for (int i = 0; i < vectors.length; i++) {
            interval /= dimensions[i];
            for (int j = 0; j < dimensions[i]; j++) {
                vectors[i][j] = j * interval;
            }
        }
        return vectors;
    }

    public int[][] getColumnRowVectors() {
        int[][] vectors = new int[B.length][dimensions.length];
        for (int i = 0; i < B.length; i++) {
            int index = i;
            for (int j = dimensions.length - 1; j >= 0; j--) {
                int factor = IntStream.range(0, j).map(k -> dimensions[k]).reduce(1, (a, b) -> a * b);
                vectors[i][j] = index / factor + intervals[j][0];
                index %= factor;
            }
        }
        return vectors;
    }

    public static void main(String[] args) {
        int[] dimensions = new int[]{4, 3, 3, 2};
        int[][] intervals = new int[][]{{0, 3}, {-7, -5}, {-2, 0}, {5, 6}};

        MultiDimensionalArray array = new MultiDimensionalArray(dimensions, intervals);
        Random random = new Random();

        // Прямой доступ
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] indexes = {random.nextInt(4), random.nextInt(3), random.nextInt(3), random.nextInt(2)};
            array.directAccess(indexes);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Прямой доступ: " + (endTime - startTime) + " мс");
        int memoryUsage = Integer.BYTES;
        System.out.println("Memory usage: " + memoryUsage + " bytes");

        // Доступ посредством векторов Айлиффа
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] indexes = {random.nextInt(4), random.nextInt(3), random.nextInt(3), random.nextInt(2)};
            array.getByAliffe(indexes);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Доступ посредством векторов Айлиффа: " + (endTime - startTime) + " мс");
        memoryUsage = Arrays.stream(dimensions).sum() * Integer.BYTES;
        System.out.println("Memory usage: " + memoryUsage + " bytes");


        long start =  System.currentTimeMillis();
        int[][] vectors = array.getColumnRowVectors();
//        for (int i = 0; i < vectors.length; i++) {
//            System.out.println(Arrays.toString(vectors[i]));
//        }
        long end = System.currentTimeMillis();
        long runtime =  (end - start);
        System.out.println("Доступ с помощью определяющих векторов: " + runtime + " мс");

// measure the memory usage of getColumnRowVectors() method
        Runtime.getRuntime().gc();
        Runtime runtimeObj = Runtime.getRuntime();
//        long freeMemory = runtimeObj.freeMemory();
//        long memoryBefore = runtimeObj.totalMemory() - runtimeObj.freeMemory();
//        vectors = array.getColumnRowVectors();
//        long memoryAfter = runtimeObj.totalMemory() - runtimeObj.freeMemory();
//        long memoryUsage1 = memoryAfter - memoryBefore;
        long memoryUsage1 = vectors.length * IntStream.of(dimensions).sum() * Integer.BYTES;
        System.out.println("Memory usage: " + memoryUsage1 + " bytes");



//        int[] indexes = {0, 1, 2, 1};
//        int element = array.getByDeterminant(indexes);
//        System.out.println(element);
    }
}


