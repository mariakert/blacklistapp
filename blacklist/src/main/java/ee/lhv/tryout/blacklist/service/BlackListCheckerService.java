package ee.lhv.tryout.blacklist.service;

import ee.lhv.tryout.blacklist.configuration.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BlackListCheckerService {

    private Parameters parameters;

    @Autowired
    public BlackListCheckerService(Parameters parameters) {
        this.parameters = parameters;
    }

    public List<String> getMatches(String name) {
        String nameWithoutPunctuation = name.toLowerCase().replaceAll("[\\p{Punct}&&[^-]]+", "");
        return parameters.getBlackList().stream()
                .filter(blackListedName -> matches(blackListedName.toLowerCase(), nameWithoutPunctuation))
                .collect(Collectors.toList());
    }

    private boolean matches(String blackListedName, String insertedName) {
        String cleanedName = removeNoise(insertedName);
        String transformedName = transform(cleanedName);
        String transformedBlackListedName = transform(blackListedName);
        if (transformedName.equalsIgnoreCase(transformedBlackListedName)) {
            return true;
        }

        String cleanedNameNoSpaces = cleanedName.replaceAll("\\s", "");
        String blackListedNameNoSpaces = blackListedName.replaceAll("\\s", "");
        if (isRotation(cleanedNameNoSpaces, blackListedNameNoSpaces)) {
            return true;
        }
        if (transformedBlackListedName.contains(transformedName)) {
            return true;
        }

        List<String> blackListedNameParts = new ArrayList<>(Arrays.asList(transformedBlackListedName.split("\\s")));
        List<String> transformedNameParts = new ArrayList<>(Arrays.asList(transformedName.split("\\s")));
        if (isMatch(blackListedNameParts, transformedNameParts)) {
            return true;
        }

        return false;
    }

    private boolean isMatch(List<String> blackListedNameParts, List<String> nameParts) {
        double matchCount = nameParts.stream()
                .filter(blackListedNameParts::contains)
                .count();
        return (matchCount / blackListedNameParts.size()) >= 0.6 && (matchCount / nameParts.size()) >= 0.6;
    }

    private boolean isRotation(String insertedName, String blackListedName) {
        return (insertedName.length() == blackListedName.length()) && (insertedName + insertedName).contains(blackListedName);
    }

    private String transform(String name) {
        name = transformBeginning(name);
        name = transformEnd(name);
        if (name.length() < 2) {
            return name;
        }
        String firstLetter = String.valueOf(name.charAt(0));
        name = transformFromSecondLetter(name);
        name = transformLastLetter(name);
        name = removeDuplicateCharacters(name);
        return firstLetter + name;
    }

    private String removeDuplicateCharacters(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (stringBuilder.toString().endsWith(String.valueOf(c))) {
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private String transformLastLetter(String name) {
        if (name.endsWith("s")) {
            name = name.substring(0, name.lastIndexOf("s"));
        }
        if (name.endsWith("ay")) {
            name = name.replaceAll("ay$", "y");
        }
        if (name.endsWith("a")) {
            name = name.substring(0, name.lastIndexOf("a"));
        }
        return name;
    }

    private String transformFromSecondLetter(String name) {
        StringBuilder remaining = new StringBuilder();
        for (int i = 1; i < name.length(); i++) {
            Pattern vowels = Pattern.compile("[aeiou]");
            if (name.substring(i).startsWith("ev")) {
                remaining.append("af");
                i++;
            } else if (vowels.matcher(String.valueOf(name.charAt(i))).matches()) {
                remaining.append("a");
            } else if (name.charAt(i) == 'q') {
                remaining.append("g");
            } else if (name.charAt(i) == 'z') {
                remaining.append("s");
            } else if (name.charAt(i) == 'm') {
                remaining.append("n");
            } else if (name.substring(i).startsWith("kn")) {
                remaining.append("n");
                i++;
            } else if (name.charAt(i) == 'k') {
                remaining.append("c");
            } else if (name.substring(i).startsWith("sch")) {
                remaining.append("sss");
                i++;
                i++;
            } else if (name.substring(i).startsWith("ph")) {
                remaining.append("ff");
                i++;
            } else if (name.charAt(i) == 'h') {
                Pattern nonVowels = Pattern.compile("[^aeiou]");
                if (nonVowels.matcher(String.valueOf(name.charAt(i - 1))).matches() || i != name.length() - 1 && nonVowels.matcher(String.valueOf(name.charAt(i + 1))).matches()) {
                    remaining.append(name.charAt(i - 1));
                }
            } else if (name.charAt(i) == 'w' && vowels.matcher(String.valueOf(name.charAt(i - 1))).matches()) {
                remaining.append(name.charAt(i - 1));
            } else if (name.charAt(i) != name.charAt(i - 1)) {
                remaining.append(name.charAt(i));
            }
        }
        return remaining.toString();
    }

    private String transformEnd(String name) {
        if (name.endsWith("ee") || name.endsWith("ie")) {
            name = name.replaceAll("[ie]e$", "y");
        } else if (name.endsWith("dt") || name.endsWith("rt") || name.endsWith("rd") || name.endsWith("nt") || name.endsWith("nd")) {
            name = name.replaceAll("[nr]d|[drn]t$", "d");
        }
        return name;
    }

    private String transformBeginning(String name) {
        if (name.startsWith("mac")) {
            name = name.replaceFirst("mac", "mcc");
        } else if (name.startsWith("kn")) {
            name = name.replaceFirst("kn", "nn");
        } else if (name.startsWith("k")) {
            name = name.replaceFirst("k", "c");
        } else if (name.startsWith("ph")) {
            name = name.replaceFirst("ph", "ff");
        } else if (name.startsWith("pf")) {
            name = name.replaceFirst("pf", "ff");
        } else if (name.startsWith("sch")) {
            name = name.replaceFirst("sch", "sss");
        }
        return name;
    }

    private String removeNoise(String name) {
        List<String> noiseWords = parameters.getNoiseWords();
        List<String> nameParts = new ArrayList<>(Arrays.asList(name.split("\\s")));
        nameParts.removeIf(noiseWords::contains);
        return String.join(" ", nameParts);
    }
}
