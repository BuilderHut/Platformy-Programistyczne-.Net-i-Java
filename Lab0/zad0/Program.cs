namespace zad0
{
    internal class Program
    {
        static void Main(string[] args)
        {
            FizzBuzz f = new FizzBuzz();
            f.Print();
        }
    }

    class FizzBuzz
    {
        private int maxnumber;

        public FizzBuzz()
        {
            maxnumber = 20;
        }

        public void Print()
        {
            for (int i = 1; i <= maxnumber; i++)
            {
                if (i % 3 == 0 && i % 5 == 0)
                {
                    Console.WriteLine("FizzBuzz");
                }
                else if (i % 3 == 0)
                {
                    Console.WriteLine("Fizz");
                }
                else if (i % 5 == 0)
                {
                    Console.WriteLine("Buzz");
                }
                else
                {
                    Console.WriteLine(i);
                }
            }
        }
    }
}