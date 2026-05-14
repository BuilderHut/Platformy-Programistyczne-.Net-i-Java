using KnapsackApp;

namespace TestProject
{
    [TestClass]
    public class ProblemTests
    {
        [TestMethod]
        public void Constructor_WithPositiveN_CreatesExactlyNItems()
        {
            int n = 5;
            int seed = 123;

            Problem problem = new Problem(n, seed);

            Assert.AreEqual(n, problem.Items.Count);
        }

        [TestMethod]
        public void Solve_WhenAtLeastOneItemFits_ReturnsAtLeastOneItem()
        {
            Problem problem = new Problem(5, 123);
            int capacity = 10;

            Result result = problem.Solve(capacity);

            Assert.IsTrue(result.SelectedItemIds.Count >= 1);
        }

        [TestMethod]
        public void Solve_WhenNoItemFits_ReturnsEmptyResult()
        {
            Problem problem = new Problem(5, 123);
            int capacity = 0;

            Result result = problem.Solve(capacity);

            Assert.AreEqual(0, result.SelectedItemIds.Count);
            Assert.AreEqual(0, result.TotalValue);
            Assert.AreEqual(0, result.TotalWeight);
        }

        [TestMethod]
        public void Solve_ForGivenSeed_ReturnsResultWithinCapacity()
        {
            Problem problem = new Problem(10, 123);
            int capacity = 15;

            Result result = problem.Solve(capacity);

            Assert.IsTrue(result.TotalWeight <= capacity);
        }

        [TestMethod]
        public void Solve_WithNegativeCapacity_ThrowsArgumentException()
        {
            Problem problem = new Problem(5, 123);

            Assert.ThrowsException<ArgumentException>(() => problem.Solve(-1));
        }

        [TestMethod]
        public void Constructor_WithNonPositiveN_ThrowsArgumentException()
        {
            Assert.ThrowsException<ArgumentException>(() => new Problem(0, 123));
        }
    }
}