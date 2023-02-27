package com.fathzer.jdbbackup.dumper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.fathzer.jdbbackup.Options;

public final class FakeJavaDumper extends DBDumperFromProcess {
	public static final List<String> CONTENT = Arrays.asList("Hello,","This is a fake db dump");
	public static boolean shouldFail;
	@Override
	public String getDBType() {
		return "java";
	}

	@Override
	protected List<String> getCommand(Options params) {
		List<String> args = shouldFail ? Arrays.asList("java",FakeJavaDumper.class.getName()) :
			Arrays.asList("java","-cp","./target/classes"+File.pathSeparator+"./target/test-classes",FakeJavaDumper.class.getName());
		return args;
	}
	
	public static void main(String[] args) {
		CONTENT.forEach(System.out::println);
	}
}