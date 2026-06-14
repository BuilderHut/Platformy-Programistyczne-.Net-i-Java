using Microsoft.EntityFrameworkCore;
using ViesLab2.Models;

namespace ViesLab2.Data;

public class AppDbContext : DbContext
{
    public DbSet<Taxpayer> Taxpayers => Set<Taxpayer>();
    public DbSet<VatCheck> VatChecks => Set<VatCheck>();

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseSqlite("Data Source=vies_lab2.db");
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Taxpayer>()
            .HasIndex(t => new { t.CountryCode, t.VatNumber })
            .IsUnique();

        modelBuilder.Entity<Taxpayer>()
            .HasMany(t => t.VatChecks)
            .WithOne(c => c.Taxpayer)
            .HasForeignKey(c => c.TaxpayerId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}
