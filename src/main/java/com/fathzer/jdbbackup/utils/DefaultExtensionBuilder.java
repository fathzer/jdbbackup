package com.fathzer.jdbbackup.utils;

import java.util.function.Function;

/** A default extension builder that does nothing if an extension is already present and adds .sql.gz if not.
 */
public class DefaultExtensionBuilder implements Function<String,CharSequence> {
	public static final Function<String,CharSequence> INSTANCE = new DefaultExtensionBuilder();

	@Override
	public CharSequence apply(String path) {
		// FIXME The test on the extension is wrong
		if (path.endsWith(".gz")) {
			return path;
		} else {
			final StringBuilder sb = new StringBuilder(path);
			if (!path.endsWith(".sql")) {
				sb.append(".sql");
			}
			sb.append(".gz");
			return sb;
		}
	}

}
