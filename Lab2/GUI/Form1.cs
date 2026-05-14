using KnapsackApp;

namespace GUI
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnGenerateAndSolve_Click(object sender, EventArgs e)
        {
            try
            {
                int n = int.Parse(txtN.Text);
                int seed = int.Parse(txtSeed.Text);
                int capacity = int.Parse(txtCapacity.Text);

                Problem problem = new Problem(n, seed);
                Result result = problem.Solve(capacity);

                txtProblem.Text = problem.ToString();
                txtResult.Text = result.ToString();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "B³¹d", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void btnClear_Click(object sender, EventArgs e)
        {
            txtN.Clear();
            txtSeed.Clear();
            txtCapacity.Clear();
            txtProblem.Clear();
            txtResult.Clear();
        }

        private void lblN_Click(object sender, EventArgs e)
        {

        }
    }
}