package property;

import property.annotation.Property;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

@Singleton
public class PropertyProducer {
    private Properties properties;

    @Property
    @Produces
    public String produceString(final InjectionPoint injectionPoint) {
        return this.properties.getProperty(getKey(injectionPoint));
    }

    @Property
    @Produces
    public int produceInt(final InjectionPoint injectionPoint) {
        return Integer.parseInt(this.properties.getProperty(getKey(injectionPoint)));
    }

    @Property
    @Produces
    public boolean produceBoolean(final InjectionPoint injectionPoint) {
        return Boolean.parseBoolean(this.properties.getProperty(getKey(injectionPoint)));
    }

    @Property
    @Produces
    public long produceLong(final InjectionPoint injectionPoint) {
        return Long.parseLong(this.properties.getProperty(getKey(injectionPoint)));
    }

    private String getKey(final InjectionPoint ip) {
        Annotated annotated = ip.getAnnotated();
        String annotationValue = annotated.getAnnotation(Property.class).value();
        boolean isAnnotationPresent = annotated.isAnnotationPresent(Property.class);
        String fieldName = ip.getMember().getName();
        return isAnnotationPresent && !annotationValue.isEmpty() ? annotationValue : fieldName;
    }

    @PostConstruct
    public void init() {
        this.properties = new Properties();
        final InputStream stream = PropertyProducer.class.getResourceAsStream("/application.properties");
        if (stream == null) {
            throw new RuntimeException("No properties!!!");
        }
        try {
            this.properties.load(stream);
        } catch (final IOException e) {
            throw new RuntimeException("Configuration could not be loaded!");
        }
    }
}
