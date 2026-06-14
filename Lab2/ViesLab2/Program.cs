using ViesLab2.Data;

namespace ViesLab2;

internal static class Program
{
    [STAThread]
    private static void Main()
    {
        ApplicationConfiguration.Initialize();

        using (var db = new AppDbContext())
        {
            db.Database.EnsureCreated();
        }

        Application.Run(new MainForm());
    }
}
