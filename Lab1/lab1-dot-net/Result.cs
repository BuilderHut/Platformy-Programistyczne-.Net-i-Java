using System.Text;

namespace KnapsackApp
{
    public class Result
    {
        public List<int> SelectedItemIds { get; }
        public int TotalValue { get; }
        public int TotalWeight { get; }

        public Result(List<int> selectedItemIds, int totalValue, int totalWeight)
        {
            SelectedItemIds = selectedItemIds;
            TotalValue = totalValue;
            TotalWeight = totalWeight;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine("Wynik:");
            sb.AppendLine($"Przedmioty w plecaku: {(SelectedItemIds.Count == 0 ? "brak" : string.Join(", ", SelectedItemIds))}");
            sb.AppendLine($"Suma wartości: {TotalValue}");
            sb.AppendLine($"Suma wagi: {TotalWeight}");

            return sb.ToString();
        }
    }
}