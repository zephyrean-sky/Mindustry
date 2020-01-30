package mindustry.annotations.impl;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import com.squareup.javapoet.*;
import mindustry.annotations.Annotations.*;
import mindustry.annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

@SupportedAnnotationTypes({"mindustry.annotations.Annotations.Subscribe", "mindustry.annotations.Annotations.EventClasses"})
public class EventProcess extends BaseProcessor{

    @Override
    public void process(RoundEnvironment env, int round) throws Exception{
        TypeSpec.Builder type = TypeSpec.classBuilder("Event").addModifiers(Modifier.PUBLIC);

        ObjectMap<TypeMirror, Array<ExecutableElement>> methods = new ObjectMap<>();

        for(Element e : env.getElementsAnnotatedWith(Subscribe.class)){
            ExecutableElement method = (ExecutableElement)e;
            if(method.getParameters().size() != 1){
                err("All event listener methods must have one event parameter.", e);
                break;
            }

            methods.getOr(method.getParameters().get(0).asType(), Array::new).add(method);
        }

        for(Element e : env.getElementsAnnotatedWith(EventClasses.class)){
            TypeElement t = (TypeElement)e;
            Array<TypeElement> inners = Array.with(t.getEnclosedElements()).select(te -> te instanceof TypeElement).as(TypeElement.class);
            for(TypeElement elem : inners){
                if(!typeu.isAssignable(elem.asType(), elementu.getTypeElement("java.lang.Enum").asType())){
                    methods.getOr(elem.asType(), Array::new);
                }
            }
        }

        methods.each((t, e) -> e.sort(Structs.comparing(m -> -m.getAnnotation(Subscribe.class).priority())));

        for(ObjectMap.Entry<TypeMirror, Array<ExecutableElement>> entry : methods.entries()){
            TypeMirror etype = entry.key;
            String name = etype.toString(); //full qualifiedevent class
            String simpleName = name.substring(name.lastIndexOf('.') + 1); //name of event class
            String baseName = simpleName.replace("Event", ""); //base name of method w/o event suffix
            String varName = Strings.camelize(baseName); //name of instantiated object
            String listName = "listeners" + baseName;
            Array<VariableElement> fields = (Array<VariableElement>)Array.with(typeu.asElement(etype).getEnclosedElements()).select(e -> e.getKind() == ElementKind.FIELD);

            MethodSpec.Builder build = MethodSpec.methodBuilder("fire" + baseName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);

            //list of custom event handlers
            type.addField(FieldSpec.builder(ParameterizedTypeName.get(Array.class, Cons.class), listName).initializer("new Array<>(2)")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL).build());

            //add registeration method for custom event handlers
            type.addMethod(MethodSpec.methodBuilder("on" + baseName)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Cons.class), TypeName.get(etype)), "listener")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC).addStatement("$L.add(listener)", listName).build());

            build.addStatement("$T $L = $T.obtain($T.class, $T::new)", etype, varName, Pools.class, etype, etype);

            for(VariableElement e : fields){
                build.addParameter(TypeName.get(e.asType()), e.getSimpleName().toString());
                build.addStatement("$L.$L = $L", varName, e.getSimpleName().toString(), e.getSimpleName());
            }

            for(ExecutableElement m : entry.value){
                //TODO resolve the objects and call the method
                build.addComment("$T.$L($L)", m.getEnclosingElement().asType(), m.getSimpleName(), varName);
            }

            //trigger custom listeners
            build.beginControlFlow("if($L.size > 0)", listName);
            build.beginControlFlow("for($T co : $L)", Cons.class, listName);
            build.addStatement("co.get($L)", varName);
            build.endControlFlow();
            build.endControlFlow();

            build.addStatement("$T.free($L)", Pools.class, varName);

            type.addMethod(build.build());
        }

        write(type);
    }
}
