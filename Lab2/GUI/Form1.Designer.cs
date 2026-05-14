namespace GUI
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            btnGenerateAndSolve = new Button();
            btnClear = new Button();
            lblN = new Label();
            lblSeed = new Label();
            lblCapacity = new Label();
            txtN = new TextBox();
            txtSeed = new TextBox();
            txtCapacity = new TextBox();
            txtProblem = new TextBox();
            txtResult = new TextBox();
            SuspendLayout();
            // 
            // btnGenerateAndSolve
            // 
            btnGenerateAndSolve.Location = new Point(442, 27);
            btnGenerateAndSolve.Name = "btnGenerateAndSolve";
            btnGenerateAndSolve.Size = new Size(359, 29);
            btnGenerateAndSolve.TabIndex = 0;
            btnGenerateAndSolve.Text = "btnGenerateAndSolve.Text = \"Generuj i rozwiąż\"\r\n";
            btnGenerateAndSolve.UseVisualStyleBackColor = true;
            // 
            // btnClear
            // 
            btnClear.Location = new Point(442, 73);
            btnClear.Name = "btnClear";
            btnClear.Size = new Size(346, 29);
            btnClear.TabIndex = 1;
            btnClear.Text = "btnClear.Text = \"Wyczyść\"\r\n";
            btnClear.UseVisualStyleBackColor = true;
            btnClear.Click += btnClear_Click;
            // 
            // lblN
            // 
            lblN.AutoSize = true;
            lblN.Location = new Point(442, 129);
            lblN.Name = "lblN";
            lblN.Size = new Size(159, 20);
            lblN.TabIndex = 2;
            lblN.Text = "\"Liczba przedmiotów:\"\r\n";
            lblN.Click += lblN_Click;
            // 
            // lblSeed
            // 
            lblSeed.AutoSize = true;
            lblSeed.Location = new Point(442, 172);
            lblSeed.Name = "lblSeed";
            lblSeed.Size = new Size(155, 20);
            lblSeed.TabIndex = 3;
            lblSeed.Text = "lblSeed.Text = \"Seed:\"\r\n";
            // 
            // lblCapacity
            // 
            lblCapacity.AutoSize = true;
            lblCapacity.Location = new Point(442, 211);
            lblCapacity.Name = "lblCapacity";
            lblCapacity.Size = new Size(272, 20);
            lblCapacity.TabIndex = 4;
            lblCapacity.Text = "lblCapacity.Text = \"Pojemność plecaka:\"\r\n";
            // 
            // txtN
            // 
            txtN.Location = new Point(145, 75);
            txtN.Name = "txtN";
            txtN.Size = new Size(125, 27);
            txtN.TabIndex = 5;
            // 
            // txtSeed
            // 
            txtSeed.Location = new Point(145, 129);
            txtSeed.Name = "txtSeed";
            txtSeed.Size = new Size(125, 27);
            txtSeed.TabIndex = 6;
            // 
            // txtCapacity
            // 
            txtCapacity.Location = new Point(145, 172);
            txtCapacity.Name = "txtCapacity";
            txtCapacity.Size = new Size(125, 27);
            txtCapacity.TabIndex = 7;
            // 
            // txtProblem
            // 
            txtProblem.Location = new Point(430, 252);
            txtProblem.Multiline = true;
            txtProblem.Name = "txtProblem";
            txtProblem.ReadOnly = true;
            txtProblem.ScrollBars = ScrollBars.Vertical;
            txtProblem.Size = new Size(125, 34);
            txtProblem.TabIndex = 8;
            // 
            // txtResult
            // 
            txtResult.Location = new Point(430, 353);
            txtResult.Multiline = true;
            txtResult.Name = "txtResult";
            txtResult.ReadOnly = true;
            txtResult.ScrollBars = ScrollBars.Vertical;
            txtResult.Size = new Size(125, 27);
            txtResult.TabIndex = 9;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(8F, 20F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(800, 450);
            Controls.Add(txtResult);
            Controls.Add(txtProblem);
            Controls.Add(txtCapacity);
            Controls.Add(txtSeed);
            Controls.Add(txtN);
            Controls.Add(lblCapacity);
            Controls.Add(lblSeed);
            Controls.Add(lblN);
            Controls.Add(btnClear);
            Controls.Add(btnGenerateAndSolve);
            Name = "Form1";
            Text = "Form1";
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Button btnGenerateAndSolve;
        private Button btnClear;
        private Label lblN;
        private Label lblSeed;
        private Label lblCapacity;
        private TextBox txtN;
        private TextBox txtSeed;
        private TextBox txtCapacity;
        private TextBox txtProblem;
        private TextBox txtResult;
    }
}
