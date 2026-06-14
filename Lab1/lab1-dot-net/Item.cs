namespace KnapsackApp
{
    public class Item
    {
        public int Id { get; }
        public int Value { get; }
        public int Weight { get; }
        public double Ratio => (double)Value / Weight;

        public Item(int id, int value, int weight)
        {
            Id = id;
            Value = value;
            Weight = weight;
        }

        public override string ToString()
        {
            return $"Przedmiot {Id}: wartość = {Value}, waga = {Weight}, v/w = {Ratio:F2}";
        }
    }
}