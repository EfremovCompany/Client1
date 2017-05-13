using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using xNet;

namespace Admin
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //const
        private const string ADMIN_KEY = "UPISsyxr1HYaDz2vuxZ81g6O7";
        //classes
        WhiteList wl = new WhiteList();

        public MainWindow()
        {
            InitializeComponent();
        }

        private void btnSyncWL_Click(object sender, RoutedEventArgs e)
        {
            string answer = wl.GET("secret=" + ADMIN_KEY + "&key=getwhitelist");
            answer = answer.Replace("]", ",");
            string[] data = answer.Substrings("{", "},");
            DataTable dt = new DataTable();
            dt.Columns.Add("ID");
            dt.Columns.Add("Номер");
            dt.Columns.Add("Адрес");
            for (int i = 0; i < data.Length; i++)
            {
                string id = data[i].Substring("Id\":", ",");
                string number = data[i].Substring("Number\":\"", "\"");
                string addr = data[i].Substring("Addr\":\"", "\"");
                dt.Rows.Add(id, number, addr);
                //MessageBox.Show(id + "\n" + number + "\n" + addr);
            }
            dtgridWL.ItemsSource = dt.DefaultView;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            DataTable dt = new DataTable();
            dt = ((DataView)dtgridWL.ItemsSource).ToTable();
            foreach(DataRow row in dt.Rows)
            {
                List<string> data = new List<string>();
                data.Add(ADMIN_KEY);
                //MessageBox.Show(row[0].ToString());
                data.Add(row[0].ToString());
                data.Add(row[1].ToString());
                data.Add(row[2].ToString());
                MessageBox.Show(wl.POST(data));

            }
        }
    }
}
