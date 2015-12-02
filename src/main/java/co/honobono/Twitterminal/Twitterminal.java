package co.honobono.Twitterminal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Twitterminal {

	static Twitter twitter = new TwitterFactory().getInstance();

	static Scanner scan = new Scanner(System.in);

	static Map<String, AccessToken> user = new HashMap<String, AccessToken>();

	static String Prefix = "";

	public static void main(String... args) {
		twitter.setOAuthConsumer("tl1bcFsYlX2mqfFowTAkZiGnh", "ra8BrrfwwPqFIMfJPn2bp3lYwgQdbvbdqHrhJdKZsOe8SSPPIE");
		System.out.println("Twitterminal Started.");
		try {
			loadConfig();
		} catch (IOException e1) {
			System.out.println("missing ConfigFile");
		}
		String com;
		String[] a;
		while (true) {
			try {
				System.out.print("> ");
				com = scan.nextLine();
				if(!com.toUpperCase().startsWith("TOGGLE"))com = Prefix + com;
				a = com.split(" ");
				if (a[0].equalsIgnoreCase("TOGGLE")) {
					if (a[1].equalsIgnoreCase("clear")) {
						Prefix = "";
						System.out.println("削除しました");
					} else {
						Prefix = a[1] + " ";
						System.out.println("設定しました");
					}
				}
				switch (a[0].toUpperCase()) {
				case "CONFIG":
					if (a[1].equalsIgnoreCase("ADD")) {
						if (user.containsKey(a[2])) {
							System.out.println("Already Registered");
						} else {
							twitter.setOAuthAccessToken(null);
							AccessToken access = null;
							RequestToken req = null;
							try {
								req = twitter.getOAuthRequestToken();
							} catch (TwitterException e) {
								System.out.println("error: 001");
							}
							while (null == access) {
								System.out.println("Please login from the following URL");
								System.out.println(req.getAuthorizationURL());
								System.out.print("Enter a PINCode: ");
								String pin = scan.nextLine();
								try {
									if (pin.length() > 0) {
										access = twitter.getOAuthAccessToken(req, pin);
									} else {
										access = twitter.getOAuthAccessToken();
									}
								} catch (TwitterException te) {
									if (401 == te.getStatusCode()) {
										System.out.println("Unable to get the access token.");
									} else {
										te.printStackTrace();
									}
								}
								twitter.setOAuthAccessToken(user.get(a[2]));
							}
							user.put(a[2], access);
							twitter.setOAuthAccessToken(user.get(a[2]));
							System.out.println("register Completed");
						}
					} else if (a[1].equalsIgnoreCase("REMOVE")) {
						if (user.containsKey(a[2])) {
							user.remove(a[2]);
							System.out.println("Removed");
						} else {
							System.out.println("Not Registered");
						}
					} else if (a[1].equalsIgnoreCase("INFO")) {
						System.out.println("Rergistered Users:");
						if (user.keySet().isEmpty()) {
							System.out.println("none");
						}
						for (Object b : user.keySet()) {
							System.out.println(b);
						}
					}
					break;
				case "TWEET":
					String s = "";
					for (int i = 1; i < a.length; i++)
						s = s + " " + a[i];
					twitter.updateStatus(s);
					break;
				case "SET":
					if (user.containsKey(a[1])) {
						twitter.setOAuthAccessToken(user.get(a[1]));
					} else {
						System.out.println("Not Registered");
					}
					break;
				case "QUIT":
				case "EXIT":
					saveConfig();
					return;
				case "SAVE":
					saveConfig();
					break;
				}
			} catch (Exception e) {
				System.out.println("Something happened");
				e.printStackTrace();
			}
		}
	}

	static File f = new File(get_path() + ".twitterminal");

	public static void saveConfig() {
		PrintWriter pw = null;
		try {
			f.createNewFile();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder sb = null;
		if (!user.isEmpty()) {
			for (Map.Entry<String, AccessToken> e : user.entrySet()) {
				sb = new StringBuilder();
				sb.append(e.getKey());
				sb.append(", ");
				sb.append(toS(e.getValue()));
				sb.append("\n");
			}
		}
		pw.print((sb == null) ? "" : sb.toString());
		pw.close();
	}

	public static String toS(AccessToken a) {
		return String.format("%s, %s", a.getToken(), a.getTokenSecret());
	}

	public static void loadConfig() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String s;
		String[] s1;
		while ((s = br.readLine()) != null) {
			s1 = s.split(", ");
			user.put(s1[0], new AccessToken(s1[1], s1[2]));
		}
		br.close();
	}

	private static String get_path() {
		String cp = System.getProperty("java.class.path");
		String fs = System.getProperty("file.separator");
		String acp = (new File(cp)).getAbsolutePath();
		int p, q;
		for (p = 0; (q = acp.indexOf(fs, p)) >= 0; p = q + 1)
			;
		return acp.substring(0, p);
	}
}