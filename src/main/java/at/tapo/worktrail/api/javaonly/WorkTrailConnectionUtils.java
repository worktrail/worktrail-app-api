package at.tapo.worktrail.api.javaonly;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkTrailConnectionUtils {
	
	private static Logger logger = Logger.getLogger(WorkTrailConnectionUtils.class.getName());

	public static StringBuilder requestDataFromUrl(URL url, byte[] tosend, String userAgent) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		if (userAgent != null) {
			urlConnection.setRequestProperty("User-Agent", userAgent);
		}
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(10000);
		// urlConnection.setChunkedStreamingMode(0);
		urlConnection.setFixedLengthStreamingMode(tosend.length);

		BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
		out.write(tosend);
		out.flush();
		out.close();

		InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[2048];
		int i;
		while ((i = reader.read(buf)) != -1) {
			sb.append(buf, 0, i);
		}
		return sb;
	}

	public static String getQuery(Collection<Entry<String, String>> encoded) {
		// http://stackoverflow.com/a/13486223/109219
		try {
			StringBuilder result = new StringBuilder();
			boolean first = true;

			for (Entry<String, String> pair : encoded) {
				if (first) {
					first = false;
				} else {
					result.append("&");
				}

				result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}

			return result.toString();
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "This should not have happened.", e);
			return null;
		}
	}

	public static List<Entry<String, String>> encodeMap(Set<Entry<String, Object>> set) {
		List<Entry<String, String>> pairs = new ArrayList<Entry<String, String>>(set.size());
		for (Map.Entry<String, Object> entry : set) {
			Object value = entry.getValue();
			if (value != null) {
				String val = null;
				if (value instanceof Date) {
					val = Long.toString(((Date) value).getTime() / 100);
				} else {
					val = value.toString();
				}
				pairs.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey(), val));
			}
		}
		return pairs;
	}
}
