package mindustry.annotations;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;

public class Utils{
    public static Types typeUtils;
    public static Elements elementUtils;
    public static Filer filer;
    public static Messager messager;

    public static String getMethodName(Element element){
        return ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString() + "." + element.getSimpleName();
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
        || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }
}
