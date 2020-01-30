package mindustry.annotations.impl;

import arc.ecs.*;
import arc.ecs.systems.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import com.squareup.javapoet.*;
import mindustry.annotations.Annotations.*;
import mindustry.annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({"mindustry.annotations.Annotations.AutoSystem"})
public class EcsProcess extends BaseProcessor{
    Array<TypeElement> types;
    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment env, int round) throws Exception{
        if(round == 0){
            types = (Array<TypeElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class)).select(e -> e instanceof TypeElement);

            Array<ExecutableElement> methods = (Array<ExecutableElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class)).select(e -> e instanceof ExecutableElement);
            for(ExecutableElement method : methods){
                Array<VariableElement> params = (Array<VariableElement>)Array.with(method.getParameters());
                Array<VariableElement> others = params.copy();

                //remove first int parameter as it's not a component...
                if(others.first().asType().toString().equals("int")){
                    others.remove(0);
                }

                TypeSpec.Builder type = TypeSpec.classBuilder(Strings.capitalize(method.getSimpleName().toString()) + "System")
                .addModifiers(Modifier.PUBLIC).addModifiers(Modifier.FINAL)
                .superclass(IteratingSystem.class)
                .addAnnotation(AutoSystem.class)
                .addMethod(MethodSpec.methodBuilder("process")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(int.class, "entity")
                    .addAnnotation(Override.class)
                    .addStatement("$L.$L($L)", method.getEnclosingElement().asType().toString(), method.getSimpleName(),
                        params.map(p -> p.asType().toString().equals("int") ? "entity" : "$m" + p.getSimpleName() + ".get(entity)").toString(", "))
                    .build())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addStatement("super(arc.ecs.Aspect.all(" + others.toString(",", p -> p.asType().toString() + ".class") + "))").build());

                for(VariableElement var : others){
                    String pname = var.getSimpleName().toString();
                    TypeMirror mirror = var.asType();
                    type.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Mapper.class), TypeName.get(mirror)), "$m" + pname).build());
                }

                write(type);
            }
        }else{
            types.addAll((Array<TypeElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class)).select(e -> e instanceof TypeElement));
            types.sort(e -> -e.getAnnotation(AutoSystem.class).priority());

            TypeSpec.Builder type = TypeSpec.classBuilder("Sys").addModifiers(Modifier.PUBLIC);

            for(TypeElement te : types){
                type.addField(TypeName.get(te.asType()), varName(te), Modifier.STATIC, Modifier.PUBLIC);
            }

            type.addField(FieldSpec.builder(ParameterizedTypeName.get(Array.class, Prov.class),
                "initializers", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("Array.with(" + types
            .map(t -> "() -> { " + varName(t) + " = new " + t.asType().toString() + "(); return " + varName(t) + ";}").toString(", ") + ")").build());

            write(type);
        }
    }

    String varName(TypeElement elem){
        return Strings.camelize(className(elem.asType()).replace("System", "").replace("Sys", "")).toLowerCase();
    }
}
