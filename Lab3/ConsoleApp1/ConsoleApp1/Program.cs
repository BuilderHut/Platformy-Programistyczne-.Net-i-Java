using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace Lab3Task2;

public sealed class Matrix
{
    private readonly double[,] _data;

    public int Rows { get; }
    public int Columns { get; }

    public Matrix(int rows, int columns)
    {
        if (rows <= 0 || columns <= 0)
            throw new ArgumentException("Liczba wierszy i kolumn musi być większa od 0.");

        Rows = rows;
        Columns = columns;
        _data = new double[rows, columns];
    }

    public double this[int row, int col]
    {
        get => _data[row, col];
        set => _data[row, col] = value;
    }

    public static Matrix CreateRandom(int rows, int columns, int minValue = 0, int maxValue = 10)
    {
        var matrix = new Matrix(rows, columns);

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                matrix[i, j] = Random.Shared.Next(minValue, maxValue);
            }
        }

        return matrix;
    }

    public void Print(string title)
    {
        Console.WriteLine(title);

        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Columns; j++)
            {
                Console.Write($"{this[i, j],8:F2}");
            }

            Console.WriteLine();
        }

        Console.WriteLine();
    }

    public bool NearlyEquals(Matrix other, double epsilon = 1e-9)
    {
        if (other.Rows != Rows || other.Columns != Columns)
            return false;

        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Columns; j++)
            {
                if (Math.Abs(this[i, j] - other[i, j]) > epsilon)
                    return false;
            }
        }

        return true;
    }
}

// =======================================================
// ZADANIE 1 - mnożenie macierzy z użyciem Parallel.For
// =======================================================

public static class MatrixMultiplierParallel
{
    public static Matrix Multiply(Matrix a, Matrix b, int threadCount)
    {
        if (a.Columns != b.Rows)
            throw new InvalidOperationException("Liczba kolumn A musi być równa liczbie wierszy B.");

        if (threadCount <= 0)
            throw new ArgumentException("Liczba wątków musi być większa od 0.");

        var result = new Matrix(a.Rows, b.Columns);

        var options = new ParallelOptions
        {
            MaxDegreeOfParallelism = Math.Min(threadCount, a.Rows)
        };

        Parallel.For(0, a.Rows, options, i =>
        {
            for (int j = 0; j < b.Columns; j++)
            {
                double sum = 0.0;

                for (int k = 0; k < a.Columns; k++)
                {
                    sum += a[i, k] * b[k, j];
                }

                result[i, j] = sum;
            }
        });

        return result;
    }
}

// =======================================================
// ZADANIE 2 - mnożenie macierzy z użyciem klasy Thread
// =======================================================

public sealed class ThreadWorkItem
{
    public required Matrix A { get; init; }
    public required Matrix B { get; init; }
    public required Matrix Result { get; init; }
    public required int StartRow { get; init; }
    public required int EndRow { get; init; }
}

public static class MatrixMultiplierThread
{
    public static Matrix Multiply(Matrix a, Matrix b, int threadCount)
    {
        if (a.Columns != b.Rows)
            throw new InvalidOperationException("Liczba kolumn A musi być równa liczbie wierszy B.");

        if (threadCount <= 0)
            throw new ArgumentException("Liczba wątków musi być większa od 0.");

        var result = new Matrix(a.Rows, b.Columns);

        int actualThreadCount = Math.Min(threadCount, a.Rows);
        var threads = new Thread[actualThreadCount];
        var workItems = SplitWork(a, b, result, actualThreadCount);

        for (int i = 0; i < actualThreadCount; i++)
        {
            ThreadWorkItem workItem = workItems[i];

            threads[i] = new Thread(() => ComputeRows(workItem));
            threads[i].Name = $"Worker-{i + 1}";
        }

        foreach (Thread thread in threads)
        {
            thread.Start();
        }

        foreach (Thread thread in threads)
        {
            thread.Join();
        }

        return result;
    }

    private static List<ThreadWorkItem> SplitWork(Matrix a, Matrix b, Matrix result, int threadCount)
    {
        var workItems = new List<ThreadWorkItem>();

        int baseRowsPerThread = a.Rows / threadCount;
        int remainingRows = a.Rows % threadCount;
        int currentRow = 0;

        for (int i = 0; i < threadCount; i++)
        {
            int rowsForThisThread = baseRowsPerThread + (i < remainingRows ? 1 : 0);

            int startRow = currentRow;
            int endRow = currentRow + rowsForThisThread;

            workItems.Add(new ThreadWorkItem
            {
                A = a,
                B = b,
                Result = result,
                StartRow = startRow,
                EndRow = endRow
            });

            currentRow = endRow;
        }

        return workItems;
    }

    private static void ComputeRows(ThreadWorkItem workItem)
    {
        for (int i = workItem.StartRow; i < workItem.EndRow; i++)
        {
            for (int j = 0; j < workItem.B.Columns; j++)
            {
                double sum = 0.0;

                for (int k = 0; k < workItem.A.Columns; k++)
                {
                    sum += workItem.A[i, k] * workItem.B[k, j];
                }

                workItem.Result[i, j] = sum;
            }
        }
    }
}

// =======================================================
// Benchmark
// =======================================================

public sealed class BenchmarkResult
{
    public string Method { get; init; } = "";
    public int MatrixSize { get; init; }
    public int ThreadCount { get; init; }
    public int Trials { get; init; }
    public double AverageMilliseconds { get; init; }
    public double Speedup { get; init; }
}

public static class BenchmarkRunner
{
    public static List<BenchmarkResult> Run(
        int[] matrixSizes,
        int[] threadCounts,
        int trialsPerConfiguration,
        bool verifyCorrectness = true)
    {
        var results = new List<BenchmarkResult>();

        int[] orderedThreadCounts = threadCounts.OrderBy(x => x).ToArray();

        if (!orderedThreadCounts.Contains(1))
            throw new ArgumentException("Lista liczby wątków musi zawierać wartość 1.");

        foreach (int size in matrixSizes)
        {
            Console.WriteLine($"Testuję macierze {size} x {size}...");

            Matrix a = Matrix.CreateRandom(size, size);
            Matrix b = Matrix.CreateRandom(size, size);

            Matrix reference = MatrixMultiplierParallel.Multiply(a, b, 1);

            RunForMethod(
                methodName: "Parallel",
                multiply: MatrixMultiplierParallel.Multiply,
                a: a,
                b: b,
                size: size,
                threadCounts: orderedThreadCounts,
                trialsPerConfiguration: trialsPerConfiguration,
                reference: reference,
                verifyCorrectness: verifyCorrectness,
                results: results);

            RunForMethod(
                methodName: "Thread",
                multiply: MatrixMultiplierThread.Multiply,
                a: a,
                b: b,
                size: size,
                threadCounts: orderedThreadCounts,
                trialsPerConfiguration: trialsPerConfiguration,
                reference: reference,
                verifyCorrectness: verifyCorrectness,
                results: results);
        }

        return results;
    }

    private static void RunForMethod(
        string methodName,
        Func<Matrix, Matrix, int, Matrix> multiply,
        Matrix a,
        Matrix b,
        int size,
        int[] threadCounts,
        int trialsPerConfiguration,
        Matrix reference,
        bool verifyCorrectness,
        List<BenchmarkResult> results)
    {
        double oneThreadAverage = 0.0;

        foreach (int threads in threadCounts)
        {
            var times = new List<double>();
            bool correctnessChecked = false;

            for (int trial = 0; trial < trialsPerConfiguration; trial++)
            {
                var stopwatch = Stopwatch.StartNew();

                Matrix result = multiply(a, b, threads);

                stopwatch.Stop();

                times.Add(stopwatch.Elapsed.TotalMilliseconds);

                if (verifyCorrectness && !correctnessChecked)
                {
                    if (!result.NearlyEquals(reference))
                    {
                        throw new Exception(
                            $"Błąd poprawności dla metody {methodName}, rozmiaru {size} i {threads} wątków.");
                    }

                    correctnessChecked = true;
                }
            }

            double average = times.Average();

            if (threads == 1)
                oneThreadAverage = average;

            results.Add(new BenchmarkResult
            {
                Method = methodName,
                MatrixSize = size,
                ThreadCount = threads,
                Trials = trialsPerConfiguration,
                AverageMilliseconds = average,
                Speedup = oneThreadAverage > 0.0 ? oneThreadAverage / average : 1.0
            });
        }
    }

    public static void PrintResults(List<BenchmarkResult> results)
    {
        Console.WriteLine();
        Console.WriteLine("WYNIKI KOŃCOWE");
        Console.WriteLine(new string('-', 92));

        Console.WriteLine(
            $"{"Metoda",-10} | {"Rozmiar",10} | {"Wątki",8} | {"Próby",6} | {"Śr. czas [ms]",14} | {"Przysp.",10}");

        Console.WriteLine(new string('-', 92));

        foreach (var result in results
                     .OrderBy(r => r.MatrixSize)
                     .ThenBy(r => r.Method)
                     .ThenBy(r => r.ThreadCount))
        {
            Console.WriteLine(
                $"{result.Method,-10} | {result.MatrixSize,10} | {result.ThreadCount,8} | {result.Trials,6} | {result.AverageMilliseconds,14:F2} | {result.Speedup,10:F2}");
        }

        Console.WriteLine(new string('-', 92));
    }
}

// =======================================================
// Program główny
// =======================================================

internal static class Program
{
    private static void Main()
    {
        int[] matrixSizes = { 100, 200, 400, 800 };

        // Dla procesora 6 rdzeni / 12 wątków logicznych:
        // 6  - liczba rdzeni fizycznych
        // 12 - liczba wątków logicznych
        // 16 - więcej niż liczba wątków logicznych, żeby sprawdzić narzut
        int[] threadCounts = { 1, 2, 4, 6, 8, 12, 16 };

        int trialsPerConfiguration = 5;

        Console.WriteLine("LAB 3 - Zadanie 1 i Zadanie 2");
        Console.WriteLine("Mnożenie macierzy z użyciem Parallel oraz Thread");
        Console.WriteLine($"Liczba logicznych procesorów: {Environment.ProcessorCount}");
        Console.WriteLine();

        RunSmallCorrectnessDemo();

        var results = BenchmarkRunner.Run(
            matrixSizes,
            threadCounts,
            trialsPerConfiguration,
            verifyCorrectness: true);

        BenchmarkRunner.PrintResults(results);

        Console.WriteLine();
        Console.WriteLine("Koniec programu.");
    }

    private static void RunSmallCorrectnessDemo()
    {
        Matrix a = Matrix.CreateRandom(4, 4, 0, 5);
        Matrix b = Matrix.CreateRandom(4, 4, 0, 5);

        Matrix resultParallel = MatrixMultiplierParallel.Multiply(a, b, 4);
        Matrix resultThread = MatrixMultiplierThread.Multiply(a, b, 4);

        a.Print("Macierz A:");
        b.Print("Macierz B:");
        resultParallel.Print("Wynik Parallel:");
        resultThread.Print("Wynik Thread:");

        Console.WriteLine($"Czy wyniki Parallel i Thread są zgodne: {resultParallel.NearlyEquals(resultThread)}");
        Console.WriteLine();
    }
}