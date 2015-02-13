package pl.javaparty.sql;


import android.content.Context;
import android.util.Log;
import pl.javaparty.prefs.Prefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PhpParser {
	private final static String SEPARATOR = ";";
	private final Context context;
	private final dbManager dbm;

	public PhpParser(Context context, dbManager dbm) {
		this.context = context;
		this.dbm = dbm;
	}

	public void parse(InputStream is) {
		if (is != null) {
			BufferedReader br = null;

			String line;
			try {
				br = new BufferedReader(new InputStreamReader(is));
				int lastID = -1;
				while ((line = br.readLine()) != null) {
					lastID = addToDatabase(line);
				}

				Prefs.setLastID(context, lastID);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * @param concert {@code String} which contains all concert data separated by const separator.
	 * @return ID of red concert, or -1 if error.
	 */
	private int addToDatabase(String concert) {
		Scanner sc = new Scanner(concert).useDelimiter(SEPARATOR);
		int id = -1;
		try {
			id = sc.nextInt();
			dbm.addConcert(sc.next(), sc.next(), sc.next(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.next(), sc.next());
		} catch (InputMismatchException e) {
			Log.i("UPDATER", "Wrong input: " + concert);
		}
		return id;
	}
}
