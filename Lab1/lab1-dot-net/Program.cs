namespace KnapsackApp
{
    public class Problem
    {
        static void Main(string[] args)
        {
            try
            {
                Console.Write("Podaj liczbę przedmiotów: ");
                int n = int.Parse(Console.ReadLine()!);

                Console.Write("Podaj seed: ");
                int seed = int.Parse(Console.ReadLine()!);

                Console.Write("Podaj pojemność plecaka: ");
                int capacity = int.Parse(Console.ReadLine()!);

                Solution problem = new Solution(n, seed);

                Console.WriteLine();
                Console.WriteLine("Wygenerowany problem:");
                Console.WriteLine(problem);

                Result result = problem.Solve(capacity);

                Console.WriteLine("Rozwiązanie zachłanne:");
                Console.WriteLine(result);
            }
            catch (FormatException)
            {
                Console.WriteLine("Błąd: wpisana wartość musi być liczbą całkowitą.");
            }
            catch (ArgumentException ex)
            {
                Console.WriteLine($"Błąd: {ex.Message}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Nieoczekiwany błąd: {ex.Message}");
            }
        }
    }
}