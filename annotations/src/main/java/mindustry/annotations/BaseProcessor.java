package mindustry.annotations;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.Diagnostic.*;
import java.io.*;
import java.util.*;

public abstract class BaseProcessor extends AbstractProcessor{
    /** Name of the base package to put all the generated classes. */
    public static final String packageName = "mindustry.gen";

    public static Types typeu;
    public static Elements elementu;
    public static Filer filer;
    public static Messager messager;

    protected int round;
    protected int rounds = 1;

    public static String getMethodName(Element element){
        return ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString() + "." + element.getSimpleName();
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
        || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        super.init(processingEnv);

        typeu = processingEnv.getTypeUtils();
        elementu = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        if(round >= rounds) return false; //only process 1 round
        try{
            process(roundEnv, round++);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }

    public void write(TypeSpec.Builder type) throws IOException{
        JavaFile.builder(packageName, type.build()).build().writeTo(filer);
    }

    public void err(String message){
        messager.printMessage(Kind.ERROR, message);
    }

    public void err(String message, Element elem){
        messager.printMessage(Kind.ERROR, message, elem);
    }

    public void process(RoundEnvironment env, int round) throws Exception{

    }
}
