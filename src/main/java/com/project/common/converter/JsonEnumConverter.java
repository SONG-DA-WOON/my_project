package kr.co.steellink.user.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Converter
public abstract class JsonEnumConverter<T extends Enum<T>> implements AttributeConverter<T, String> {

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null)
            return null;

        Method codeMethod;
        try {
            codeMethod = attribute.getClass().getMethod("getCode");
        } catch (NoSuchMethodException e) {
            return null;
        }

        try {
            Object invoke = codeMethod.invoke(attribute);

            return invoke != null ? invoke.toString() : null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convertToEntityAttribute(String dbData) {
        if (StringUtils.hasText(dbData)) {
            Class<?> clazz = GenericTypeResolver.resolveTypeArgument(getClass(), JsonEnumConverter.class);
            if (clazz == null)
                throw new RuntimeException("Cannot resolve type argument: " + getClass().getName());

            try {
                Method ofCode = clazz.getMethod("ofCode", String.class);
                Object invoke = ofCode.invoke(getClass(), dbData);

                return (T) invoke;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

}
