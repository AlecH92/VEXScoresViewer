package com.hilltoprobotics.VEXViewer;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import print.color.ColoredPrinterWIN;
import print.color.Ansi.*;

public class Main {
	public final static void clearConsole() {
		try {
			final String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				Runtime.getRuntime().exec("cls");
			}
			else {
				Runtime.getRuntime().exec("clear");
			}
		}
		catch (final Exception e) {
		}
}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		    String jsonText = readAll(rd);
		    JSONArray json = new JSONArray(jsonText);
		    return json;
		    }
		finally {
			is.close();
		}
	}
	
	public static JSONObject readJsonOFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		    String jsonText = readAll(rd);
		    JSONObject json = new JSONObject(jsonText);
		    return json;
		    }
		finally {
			is.close();
		}
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		while(true) {
			
			JSONObject matchlist = readJsonOFromUrl("http://192.168.1.238/api/matches/divisions/1?json=1&page=1");
			JSONArray thelist = matchlist.getJSONArray("matchList");
			
			for (int j=0; j < thelist.length(); j++) {
				JSONObject thematch = thelist.getJSONObject(j);
				JSONObject matchinfo = thematch.getJSONObject("info");
				JSONObject matchscores = thematch.getJSONObject("score");
				JSONObject roundinfo = matchinfo.getJSONObject("matchTuple");
				JSONArray alliance = matchinfo.getJSONArray("alliances");
				JSONObject alliance1 = alliance.getJSONObject(0);
				JSONObject alliance2 = alliance.getJSONObject(1);
				JSONArray teams = alliance1.getJSONArray("teams");
				JSONObject team1 = teams.getJSONObject(0);
				JSONObject team2 = teams.getJSONObject(1);
				JSONArray teams2 = alliance2.getJSONArray("teams");
				JSONObject team3 = teams2.getJSONObject(0);
				JSONObject team4 = teams2.getJSONObject(1);
				ColoredPrinterWIN cp = new ColoredPrinterWIN.Builder(1, false).foreground(FColor.BLACK).background(BColor.WHITE).build();
				if(roundinfo.getInt("round") == 1) {
					cp.print("Practice ");
					cp.print("Match ");cp.print(roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 2) {
					cp.print("Qualification ");
					cp.print("Match ");cp.print(roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 9) {
					cp.print("Match R128 ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 8) {
					cp.print("Match R64 ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 7) {
					cp.print("Match R32 ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 6) {
					cp.print("Match R16 ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 3) {
					cp.print("Match QF ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 4) {
					cp.print("Match SF ");cp.print(roundinfo.getInt("instance") +"-" + roundinfo.getInt("match") + ": ");
				}
				else if(roundinfo.getInt("round") == 5) {
					cp.print("Match F-");cp.print(roundinfo.getInt("match") + ":    ");
				}
		        cp.print("Red Team: ", Attribute.NONE, FColor.RED, BColor.WHITE);
				cp.print(team1.getString("number") + "\t" + team2.getString("number") + "   ", Attribute.NONE, FColor.RED, BColor.WHITE);
				cp.clear();
		        cp.print("\t" + "Blue Team: ", Attribute.NONE, FColor.BLUE, BColor.WHITE);
				cp.print(team3.getString("number") + "\t" + team4.getString("number") + "\t", Attribute.NONE, FColor.BLUE, BColor.WHITE);
				if(matchinfo.getInt("state") == 4) {
					if(matchscores.getInt("winner") == 0) {
						cp.println("Winner: Tie", Attribute.NONE, FColor.BLACK, BColor.WHITE);
					}
					else if(matchscores.getInt("winner") == 1) {
						cp.print("Winner: ", Attribute.NONE, FColor.BLACK, BColor.WHITE);
						cp.println("Red", Attribute.NONE, FColor.RED, BColor.WHITE);
					}
					if(matchscores.getInt("winner") == 2) {
						cp.print("Winner: ", Attribute.NONE, FColor.BLACK, BColor.WHITE);
						cp.println("Blue", Attribute.NONE, FColor.BLUE, BColor.WHITE);
					}
				}
				else {
					if(matchinfo.getLong("timeScheduled") == 0) {
						cp.println("");
					}
					else {
						Date startDate = new Date(matchinfo.getLong("timeScheduled") * 1000L);
						SimpleDateFormat sdf = new SimpleDateFormat("h:mm a"); // the format of your date
						sdf.setTimeZone(TimeZone.getTimeZone("GMT-7")); // give a timezone reference for formating (see comment at the bottom
						String formattedDate = sdf.format(startDate);
						cp.print("Scheduled Time: " + formattedDate, Attribute.NONE, FColor.BLACK, BColor.WHITE);
						cp.println("");
					}
				}
				
				cp.clear();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
	}
}