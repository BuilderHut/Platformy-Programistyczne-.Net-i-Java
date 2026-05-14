using System.Text;

namespace KnapsackApp
{
    public class Solution
    {
        public int NumberOfItems { get; }
        public List<Item> Items { get; }

        public Solution(int n, int seed)
        {
            if (n <= 0)
            {
                throw new ArgumentException("Liczba przedmiotów musi być większa od 0.");
            }

            NumberOfItems = n;
            Items = new List<Item>();

            Random random = new Random(seed);

            for (int i = 1; i <= n; i++)
            {
                int value = random.Next(1, 11);
                int weight = random.Next(1, 11);

                Items.Add(new Item(i, value, weight));
            }
        }

        public Result Solve(int capacity)
        {
            if (capacity < 0)
            {
                throw new ArgumentException("Pojemność plecaka nie może być ujemna.");
            }

            List<Item> sortedItems = Items
                .OrderByDescending(item => item.Ratio)
                .ThenByDescending(item => item.Value)
                .ToList();

            List<int> selectedIds = new List<int>();
            int totalValue = 0;
            int totalWeight = 0;

            foreach (Item item in sortedItems)
            {
                if (totalWeight + item.Weight <= capacity)
                {
                    selectedIds.Add(item.Id);
                    totalWeight += item.Weight;
                    totalValue += item.Value;
                }

                if (totalWeight == capacity)
                {
                    break;
                }
            }

            return new Result(selectedIds, totalValue, totalWeight);
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine($"Liczba przedmiotów: {NumberOfItems}");
            sb.AppendLine("Lista przedmiotów:");

            foreach (Item item in Items)
            {
                sb.AppendLine(item.ToString());
            }

            return sb.ToString();
        }
    }
}