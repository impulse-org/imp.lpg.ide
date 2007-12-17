package org.eclipse.imp.lpg.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.language.IAnnotationTypeInfo;

public class LPGAnnotationTypeInfo implements IAnnotationTypeInfo {
    // TODO merge in builder-related marker type info logic
    private static List<String> problemMarkerTypes= new ArrayList<String>();

    public List getProblemMarkerTypes() {
        return problemMarkerTypes;
    }

    public void addProblemMarkerType(String problemMarkerType) {
        problemMarkerTypes.add(problemMarkerType);
    }

    public void removeProblemMarkerType(String problemMarkerType) {
        problemMarkerTypes.remove(problemMarkerType);
    }
}
