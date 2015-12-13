package com.fathzer.jdbbackup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultNameDecoder {
	private static final Pattern PATTERN = Pattern.compile("\\{(\\p{Lower}+)=([^\\}]+)\\}");
	public static final DefaultNameDecoder INSTANCE = new DefaultNameDecoder();

	protected DefaultNameDecoder() {
		super();
	}

	public String decode(String content) throws IllegalNamePattern {
		Matcher m = PATTERN.matcher(content);
		StringBuilder sb = new StringBuilder();
		int previous = 0;
		while (m.find()) {
//			System.out.print("From "+m.start()+" to "+m.end()+" "+content.substring(m.start(), m.end())+": ");
			if (previous!=m.start()) {
				sb.append(content.substring(previous, m.start()));
			}
			sb.append(decode(m.group(1), m.group(2)));
			previous = m.end();
		}
		if (previous<content.length()) {
			sb.append(content.substring(previous));
		}
		String result = sb.toString();
		if (!result.endsWith(".gz")) {
			if (!result.endsWith(".sql")) { //TODO May depends on database ?
				sb.append(".sql");
			}
			sb.append(".gz");
		}
		return sb.toString();
	}
	
	protected CharSequence decode(String option, String value) throws IllegalNamePattern {
		if ("d".equals(option)) {
			try {
				return new SimpleDateFormat(value).format(new Date());
			} catch (IllegalArgumentException e) {
				throw new IllegalNamePattern(value+" is not a valid argument for "+option+" pattern");
			}
		} else {
			throw new IllegalNamePattern(option+" is not a valid pattern name");
		}
	}
}
