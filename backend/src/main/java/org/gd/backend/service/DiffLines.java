package org.gd.backend.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class DiffLines {

    private static final Pattern HUNK_HEADER =
            Pattern.compile("^@@ -\\d+(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@");

    private DiffLines() {}

    public static Set<Integer> commentableLines(String patch){
        Set<Integer> lines = new HashSet<>();
        if(patch == null){
            return lines;
        }
        int current = 0;
        for(String raw:patch.split("\n")){
            Matcher header =  HUNK_HEADER.matcher(raw);
            if( header.find())
            {
                current = Integer.parseInt(header.group(1));
                continue;
            }
            if(raw.startsWith("-") || raw.startsWith("\\")){continue;}
            lines.add(current);
            current++;
        }
        return lines;

    }
}
