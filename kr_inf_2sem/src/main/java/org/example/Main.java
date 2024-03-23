package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(new File("src\\main\\java\\org\\example\\schedule.txt").toPath(), Charset.defaultCharset());

        Map<String, List<Program>> programsMap = new HashMap<>();
        List<Program> allPrograms = new ArrayList<>();
        String currentChannel = "";

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("#")) {
                currentChannel = lines.get(i).substring(1).trim();
                continue;
            }
            if (Character.isDigit(lines.get(i).charAt(0))) {
                byte hour = Byte.parseByte(lines.get(i).split(":")[0]);
                byte minutes = Byte.parseByte(lines.get(i).split(":")[1]);
                BroadcastsTime time = new BroadcastsTime(hour, minutes);
                String title = lines.get(i + 1);
                Program program = new Program(currentChannel, time, title);
                programsMap.computeIfAbsent(currentChannel, k -> new ArrayList<>()).add(program);
                allPrograms.add(program);
            }
        }
    }

    static void showSortedPrograms(List<Program> allPrograms) {
        allPrograms.sort(Comparator.comparing(Program::getTime));
        for (Program program : allPrograms) {
            System.out.println(program.getChannel() + ": " + program.getTime() + " - " + program.getTitle());
        }
    }

    static <LocalTime> void showOnGoing(List<Program> allPrograms) {
        allPrograms.sort(Comparator.comparing(Program::getTime));
        LocalTime currentTime = LocalTime.now();
        byte hour = Byte.parseByte(String.valueOf(currentTime.getClass()));
        byte minutes = Byte.parseByte(String.valueOf(currentTime.getMinute()));
        BroadcastsTime current = new BroadcastsTime(hour, minutes);

        for (int i = 0; i < allPrograms.size(); i++) {
            if (allPrograms.get(i).getTime().compareTo(current) <= 0 && allPrograms.get(i + 1).getTime().compareTo(current) >= 0) {
                System.out.println(allPrograms.get(i).getChannel() + ": " + allPrograms.get(i).getTime() + " - " + allPrograms.get(i).getTitle());
            }
        }
    }

    static void searchProgram(String searchTerm, List<Program> allPrograms) {
        
        List<Program> foundPrograms = findProgramsByTitle(allPrograms, searchTerm);

        System.out.println("Найденные программы с названием '" + searchTerm + "':");
        for (Program program : foundPrograms) {
            System.out.println(program.getChannel() + ": " + program.getTime() + " - " + program.getTitle());
        }
    }
    private static List<Program> findProgramsByTitle(List<Program> allPrograms, String title) {
        List<Program> foundPrograms = new ArrayList<>();
        for (Program program : allPrograms) {
            if (program.getTitle().contains(title)) {
                foundPrograms.add(program);
            }
        }
        return foundPrograms;
    }
}