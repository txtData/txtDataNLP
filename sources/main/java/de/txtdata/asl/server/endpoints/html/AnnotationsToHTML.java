package de.txtdata.asl.server.endpoints.html;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.server.endpoints.api.models.Entity;
import de.txtdata.asl.util.dataStructures.KeyValuePairList;
import de.txtdata.asl.util.misc.PrettyString;

import java.util.ArrayList;
import java.util.List;

public class AnnotationsToHTML {

    public static String spanOpen = "<span class=\"label-<!--TYPE-->\">";
    public static String spanClose = "</span>";
    public static String toReplace = "<!--TYPE-->";

    public static KeyValuePairList<String,String> typesToColors;

    static{
        typesToColors = new KeyValuePairList<>();
        typesToColors.add("PERSON","danger");
        typesToColors.add("LOCATION","success");
        typesToColors.add("ORGANIZATION","info");
        typesToColors.add("Keyword","warning");
        typesToColors.add("NounChunk","warning");
        typesToColors.add("Other","default");
    }

    public static String getHTMLText(String text, List<Annotation> annotations){
        return getHTMLText(text, annotations, false);
    }

    public static String getHTMLText(String text, List<Annotation> annotations, boolean removeOverlaps){
        if (annotations==null) annotations = new ArrayList<>();
        if (removeOverlaps) {
            annotations = sortAnnotations(annotations);
            annotations = removeOverlaps(annotations);
        }
        String original = text;
        String result = text;
        int added = 0;
        for (Annotation annotation : annotations){
            String before = result.substring(0, annotation.getStarts() + added);
            String after = result.substring(annotation.getEnds() + added, original.length() + added);
            String between = result.substring(annotation.getStarts() + added, annotation.getEnds() + added);
            String color = typesToColors.get(annotation.getType());
            if (color==null){
                color = typesToColors.get("Other");
            }
            String open = spanOpen.replaceAll(toReplace, color);
            result = before + open + between + spanClose + after;
            added = added + open.length() + spanClose.length();
        }
        result =  "<b>Annotations:</b><br><br>"+ result;
        return result;
    }

    public static String getHTMLTable(List<Entity> entities){
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Noun Chunks:</b><br><br>");
        if (entities.isEmpty()){
            sb.append("\n<br>None");
            return sb.toString();
        }
        sb.append("\n      <table class=\"table table-bordered table-hover\">\n        <tr> <th>#</th> <th>Name</th> <th>Typ</th> <th>Score</th> <th>Link</th> </tr>\n");
        int i = 0;
        for (Entity entity : entities){
            i++;
            String color = typesToColors.get(entity.type);
            sb.append("        <tr class=\""+color+"\">");
            sb.append(createCell(i + ""));
            sb.append(createCell(entity.surface));
            sb.append(createCell(entity.type));
            sb.append(createCell(PrettyString.create(entity.score,2,3)));
            sb.append(createCell(createLink(entity.surface)));
            sb.append("</tr>\n");
        }
        sb.append("      </table>\n      ");
        return sb.toString();
    }

    private static String createCell(String value){
        if (value==null) value = "";
        return "<td>"+value+"</td>";
    }

    private static String createLink(String value){
        if (value==null) return "";
        value = "en.wikipedia.org/wiki/" + value.replaceAll(" ","_");
        return "<a href=\"http://"+value+"\" target=\"nada\">"+value+"</a>";
    }

    private static List<Annotation> sortAnnotations(List<Annotation> annotations){
        List<Annotation> sorted = new ArrayList<>();
        for (Annotation toAdd : annotations){
            int i = 0;
            for (Annotation current : sorted){
                if (toAdd.getStarts() < current.getStarts()){
                    break;
                }else if (toAdd.getStarts() == current.getStarts()){
                    if (toAdd.getEnds() >= current.getEnds()){
                        break;
                    }
                }
                i++;
            }
            sorted.add(i,toAdd);
        }
        return sorted;
    }

    /**
     * Assumes the list is sorted. If this cannot be guaranteed, call sortAnnotations() beforehand.
     */
    private static List<Annotation> removeOverlaps(List<Annotation> annotations){
        List<Annotation> results = new ArrayList<>();
        int lastEnd = -1;
        for (Annotation annotation : annotations){
            if (annotation.getStarts() >lastEnd){
                results.add(annotation);
                lastEnd = annotation.getEnds();
            }
        }
        return results;
    }

    private static String capitalizeFirst(String str){
        char[] stringArray = str.toLowerCase().toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        str = new String(stringArray);
        return str;
    }
}
