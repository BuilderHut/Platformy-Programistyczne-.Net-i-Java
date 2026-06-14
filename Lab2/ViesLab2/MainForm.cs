using Microsoft.EntityFrameworkCore;
using ViesLab2.Data;
using ViesLab2.Models;
using ViesLab2.Services;

namespace ViesLab2;

public class MainForm : Form
{
    private readonly AppDbContext _db = new();
    private readonly ViesService _viesService = new();

    private readonly TextBox _countryTextBox = new() { Text = "PL" };
    private readonly TextBox _vatTextBox = new() { Text = "5260309174" };
    private readonly TextBox _nameTextBox = new();
    private readonly TextBox _addressTextBox = new();
    private readonly CheckBox _validCheckBox = new() { Text = "Aktywny VAT" };
    private readonly DataGridView _grid = new();

    public MainForm()
    {
        Text = "Laboratorium 2 - VIES API + SQLite";
        Width = 1050;
        Height = 650;
        StartPosition = FormStartPosition.CenterScreen;

        BuildUi();
        RefreshGrid();
    }

    private void BuildUi()
    {
        var main = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 1,
            RowCount = 2,
            Padding = new Padding(10)
        };
        main.RowStyles.Add(new RowStyle(SizeType.Absolute, 210));
        main.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
        Controls.Add(main);

        var top = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 4,
            RowCount = 5,
            AutoSize = true
        };
        top.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 120));
        top.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
        top.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 140));
        top.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
        main.Controls.Add(top, 0, 0);

        AddLabel(top, "Kraj:", 0, 0);
        top.Controls.Add(_countryTextBox, 1, 0);
        AddLabel(top, "VAT ID:", 2, 0);
        top.Controls.Add(_vatTextBox, 3, 0);

        AddLabel(top, "Nazwa:", 0, 1);
        top.Controls.Add(_nameTextBox, 1, 1);
        top.SetColumnSpan(_nameTextBox, 3);

        AddLabel(top, "Adres:", 0, 2);
        _addressTextBox.Multiline = true;
        _addressTextBox.Height = 45;
        top.Controls.Add(_addressTextBox, 1, 2);
        top.SetColumnSpan(_addressTextBox, 3);

        top.Controls.Add(_validCheckBox, 1, 3);

        var buttons = new FlowLayoutPanel
        {
            Dock = DockStyle.Fill,
            FlowDirection = FlowDirection.LeftToRight,
            AutoSize = true
        };

        var fetchButton = new Button { Text = "Pobierz z API / z bazy", Width = 160 };
        fetchButton.Click += async (_, _) => await FetchFromApiOrDbAsync();

        var manualAddButton = new Button { Text = "Dodaj ręcznie", Width = 120 };
        manualAddButton.Click += (_, _) => AddManual();

        var showAllButton = new Button { Text = "Pokaż wszystko", Width = 120 };
        showAllButton.Click += (_, _) => RefreshGrid();

        var validOnlyButton = new Button { Text = "Filtr: aktywni", Width = 120 };
        validOnlyButton.Click += (_, _) => RefreshGrid(_db.Taxpayers.Include(t => t.VatChecks).Where(t => t.Valid).ToList());

        var sortButton = new Button { Text = "Sortuj po nazwie", Width = 130 };
        sortButton.Click += (_, _) => RefreshGrid(_db.Taxpayers.Include(t => t.VatChecks).OrderBy(t => t.Name).ToList());

        var deleteButton = new Button { Text = "Usuń zaznaczony", Width = 130 };
        deleteButton.Click += (_, _) => DeleteSelected();

        buttons.Controls.AddRange(new Control[]
        {
            fetchButton,
            manualAddButton,
            showAllButton,
            validOnlyButton,
            sortButton,
            deleteButton
        });

        top.Controls.Add(buttons, 0, 4);
        top.SetColumnSpan(buttons, 4);

        _grid.Dock = DockStyle.Fill;
        _grid.ReadOnly = true;
        _grid.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
        _grid.MultiSelect = false;
        _grid.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
        main.Controls.Add(_grid, 0, 1);
    }

    private static void AddLabel(TableLayoutPanel panel, string text, int column, int row)
    {
        panel.Controls.Add(new Label
        {
            Text = text,
            Dock = DockStyle.Fill,
            TextAlign = ContentAlignment.MiddleLeft
        }, column, row);
    }

    private async Task FetchFromApiOrDbAsync()
    {
        string country = NormalizeCountry();
        string vat = NormalizeVat();

        if (!ValidateRequired(country, vat))
            return;

        Taxpayer? existing = _db.Taxpayers
            .Include(t => t.VatChecks)
            .FirstOrDefault(t => t.CountryCode == country && t.VatNumber == vat);

        if (existing is not null)
        {
            existing.VatChecks.Add(new VatCheck { CheckedAt = DateTime.Now });
            _db.SaveChanges();
            FillForm(existing);
            RefreshGrid();
            MessageBox.Show("Rekord był już w bazie, więc nie pobrano go ponownie z API. Dodano tylko historię sprawdzenia.");
            return;
        }

        try
        {
            Taxpayer taxpayer = await _viesService.GetTaxpayerAsync(country, vat);
            taxpayer.VatChecks.Add(new VatCheck { CheckedAt = DateTime.Now });

            _db.Taxpayers.Add(taxpayer);
            _db.SaveChanges();

            FillForm(taxpayer);
            RefreshGrid();
            MessageBox.Show("Pobrano dane z API i zapisano w bazie SQLite.");
        }
        catch (Exception ex)
        {
            MessageBox.Show("Nie udało się pobrać danych z API. Szczegóły: " + ex.Message);
        }
    }

    private void AddManual()
    {
        string country = NormalizeCountry();
        string vat = NormalizeVat();

        if (!ValidateRequired(country, vat))
            return;

        bool exists = _db.Taxpayers.Any(t => t.CountryCode == country && t.VatNumber == vat);
        if (exists)
        {
            MessageBox.Show("Taki rekord już istnieje w bazie.");
            return;
        }

        var taxpayer = new Taxpayer
        {
            CountryCode = country,
            VatNumber = vat,
            Name = _nameTextBox.Text.Trim(),
            Address = _addressTextBox.Text.Trim(),
            Valid = _validCheckBox.Checked
        };

        taxpayer.VatChecks.Add(new VatCheck { CheckedAt = DateTime.Now });
        _db.Taxpayers.Add(taxpayer);
        _db.SaveChanges();
        RefreshGrid();
        MessageBox.Show("Dodano rekord ręcznie do bazy.");
    }

    private void DeleteSelected()
    {
        if (_grid.CurrentRow?.Cells["Id"].Value is null)
        {
            MessageBox.Show("Zaznacz rekord do usunięcia.");
            return;
        }

        int id = Convert.ToInt32(_grid.CurrentRow.Cells["Id"].Value);
        Taxpayer? taxpayer = _db.Taxpayers.FirstOrDefault(t => t.Id == id);

        if (taxpayer is null)
            return;

        _db.Taxpayers.Remove(taxpayer);
        _db.SaveChanges();
        RefreshGrid();
    }

    private void RefreshGrid(IEnumerable<Taxpayer>? taxpayers = null)
    {
        var data = (taxpayers ?? _db.Taxpayers.Include(t => t.VatChecks).ToList())
            .Select(t => new
            {
                t.Id,
                Kraj = t.CountryCode,
                VAT = t.VatNumber,
                Nazwa = t.Name,
                Adres = t.Address,
                Aktywny = t.Valid,
                LiczbaSprawdzen = t.VatChecks.Count,
                OstatnieSprawdzenie = t.VatChecks
                    .OrderByDescending(c => c.CheckedAt)
                    .Select(c => c.CheckedAt.ToString("yyyy-MM-dd HH:mm"))
                    .FirstOrDefault()
            })
            .ToList();

        _grid.DataSource = data;
    }

    private void FillForm(Taxpayer taxpayer)
    {
        _countryTextBox.Text = taxpayer.CountryCode;
        _vatTextBox.Text = taxpayer.VatNumber;
        _nameTextBox.Text = taxpayer.Name;
        _addressTextBox.Text = taxpayer.Address;
        _validCheckBox.Checked = taxpayer.Valid;
    }

    private bool ValidateRequired(string country, string vat)
    {
        if (string.IsNullOrWhiteSpace(country) || string.IsNullOrWhiteSpace(vat))
        {
            MessageBox.Show("Podaj kod kraju i numer VAT.");
            return false;
        }

        if (country.Length != 2)
        {
            MessageBox.Show("Kod kraju powinien mieć 2 znaki, np. PL, DE, CZ.");
            return false;
        }

        return true;
    }

    private string NormalizeCountry() => _countryTextBox.Text.Trim().ToUpperInvariant();

    private string NormalizeVat() => _vatTextBox.Text.Trim().Replace(" ", "");

    protected override void OnFormClosed(FormClosedEventArgs e)
    {
        _db.Dispose();
        base.OnFormClosed(e);
    }
}
