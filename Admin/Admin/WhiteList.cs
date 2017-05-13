using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Admin
{
    class WhiteList
    {
        public WhiteList()
        {

        }

        public string POST(List<string> data)
        {
            using (var webClient = new WebClient())
            {
                var pars = new NameValueCollection();
                pars.Add("secret", data[0]);
                pars.Add("id", data[1]);
                pars.Add("number", data[2]);
                pars.Add("addr", data[3]);
                pars.Add("key", "setwhitelist");
                var response = webClient.UploadValues("http://localhost:8080/admin", pars);
                string str = System.Text.Encoding.UTF8.GetString(response);
                return str;
            }
        }

        public string GET(string Data)
        {
            WebRequest req = WebRequest.Create("http://localhost:8080/admin" + "?" + Data);
            WebResponse resp = req.GetResponse();
            Stream stream = resp.GetResponseStream();
            StreamReader sr = new StreamReader(stream);
            string Out = sr.ReadToEnd();
            sr.Close();
            return Out;
        }
    }
}
