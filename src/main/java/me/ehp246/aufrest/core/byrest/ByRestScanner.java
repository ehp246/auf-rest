package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

final class ByRestScanner {
    private final Class<? extends Annotation> enabler;
    private final Class<? extends Annotation> enablee;
    private final AnnotationMetadata metadata;

    public ByRestScanner(Class<? extends Annotation> enabler, Class<? extends Annotation> enablee,
            AnnotationMetadata metaData) {
        super();
        this.enabler = enabler;
        this.enablee = enablee;
        this.metadata = metaData;
    }

    public Stream<BeanDefinition> perform() {
        final var enablerAttributes = metadata.getAnnotationAttributes(this.enabler.getCanonicalName());
        if (enablerAttributes == null) {
            return Stream.ofNullable(null);
        }

        final var provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(this.enablee));

        Stream<String> scanThese = null;
        final var base = (Class<?>[]) enablerAttributes.get("scan");

        if (base.length > 0) {
            scanThese = Stream.of(base).map(baseClass -> baseClass.getPackage().getName()).distinct();
        } else {
            final var baseName = metadata.getClassName();
            scanThese = Stream.of(baseName.substring(0, baseName.lastIndexOf(".")));
        }

        return scanThese.flatMap(packageName -> provider.findCandidateComponents(packageName).stream());
    }
}
