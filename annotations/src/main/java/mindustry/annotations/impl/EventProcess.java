package mindustry.annotations.impl;

import arc.struct.*;
import com.squareup.javapoet.*;
import mindustry.annotations.Annotations.*;
import mindustry.annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

@SupportedAnnotationTypes("mindustry.annotations.Annotations.Subscribe")
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

        for(ObjectMap.Entry<TypeMirror, Array<ExecutableElement>> entry : methods.entries()){
            TypeMirror etype = entry.key;
            String name = etype.toString();
            String simpleName = name.substring(name.lastIndexOf('.') + 1);
            String baseName = simpleName.replace("Event", "");
            Array<VariableElement> fields = (Array<VariableElement>)Array.with(typeu.asElement(etype).getEnclosedElements()).select(e -> e.getKind() == ElementKind.FIELD);
            //TODO implement fully

            MethodSpec.Builder build = MethodSpec.methodBuilder("fire" + baseName).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        }

        write(type);
    }
}
