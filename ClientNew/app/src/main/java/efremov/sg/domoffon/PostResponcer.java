package efremov.sg.domoffon;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import static android.provider.Settings.Global.getString;


/**
 * Created by Aleksey on 06.05.2017.
 */

public class PostResponcer extends AsyncTask<Void, Void, Boolean> {
    private String response;
    public boolean isEnd = false;

    private final Map<String, String> mParams;
    private final String mIP;

    PostResponcer(Map<String, String> params, String ip) {
        mParams = params;
        mIP = ip;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Thread.sleep(2000);
            URL myURL = new URL(mIP);
            HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = Uri.parse(mIP).buildUpon();
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer resp = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                resp.append(inputLine);
            }
            in.close();

            response = resp.toString();
            // Simulate network access
        } catch (InterruptedException e) {
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = e.getMessage();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            response = e.getMessage();
        } catch (ProtocolException e) {
            e.printStackTrace();
            response = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            response = e.getMessage();
        }
        return true;
    }

    public String GetResponce()
    {
        return response;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        isEnd = true;
        if (!success) {
            response = "error";
        }
    }
}
