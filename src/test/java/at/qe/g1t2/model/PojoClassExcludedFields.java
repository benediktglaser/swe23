package at.qe.g1t2.model;
//This class is from: https://gist.github.com/patrickhuy/0392c54f52ad921bcf0152fe3cbb4d7c
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.PojoPackage;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PojoClassExcludedFields implements PojoClass {
    private final PojoClass clazz;
    private final Set<String> excluded;

    PojoClassExcludedFields(PojoClass clazz, Set<String> excluded) {
        this.clazz = clazz;
        this.excluded = excluded;
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return clazz.isAbstract();
    }

    @Override
    public boolean isConcrete() {
        return clazz.isConcrete();
    }

    @Override
    public boolean isEnum() {
        return clazz.isEnum();
    }

    @Override
    public boolean isArray() {
        return clazz.isArray();
    }

    @Override
    public boolean isFinal() {
        return clazz.isFinal();
    }

    @Override
    public boolean isSynthetic() {
        return clazz.isSynthetic();
    }

    @Override
    public List<PojoField> getPojoFields() {
        return clazz.getPojoFields().stream().filter(f -> !excluded.contains(f.getName())).collect(Collectors.toList());
    }

    @Override
    public List<PojoField> getPojoFieldsAnnotatedWith(Class<? extends Annotation> annotation) {
        return clazz.getPojoFieldsAnnotatedWith(annotation);
    }

    @Override
    public List<PojoMethod> getPojoMethods() {
        return clazz.getPojoMethods();
    }

    @Override
    public List<PojoMethod> getPojoMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
        return clazz.getPojoMethodsAnnotatedWith(annotation);
    }

    @Override
    public List<PojoMethod> getPojoConstructors() {
        return clazz.getPojoConstructors();
    }



    @Override
    public boolean extendz(Class<?> type) {
        return clazz.extendz(type);
    }

    @Override
    public PojoClass getSuperClass() {
        return clazz.getSuperClass();
    }

    @Override
    public List<PojoClass> getInterfaces() {
        return clazz.getInterfaces();
    }

    @Override
    public PojoPackage getPackage() {
        return clazz.getPackage();
    }

    @Override
    public Class<?> getClazz() {
        return clazz.getClazz();
    }

    @Override
    public boolean isNestedClass() {
        return clazz.isNestedClass();
    }

    @Override
    public boolean isStatic() {
        return clazz.isStatic();
    }

    @Override
    public void copy(Object from, Object to) {
        clazz.copy(from, to);
    }

    @Override
    public String toString(Object instance) {
        return clazz.toString(instance);
    }

    @Override
    public String getSourcePath() {
        return clazz.getSourcePath();
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public List<? extends Annotation> getAnnotations() {
        return clazz.getAnnotations();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    @Override
    public boolean isPrivate() {
        return clazz.isPrivate();
    }

    @Override
    public boolean isProtected() {
        return clazz.isProtected();
    }

    @Override
    public boolean isPublic() {
        return clazz.isPublic();
    }

    @Override
    public boolean isPackagePrivate() {
        return clazz.isPackagePrivate();
    }


}